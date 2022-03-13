package ru.swap.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.swap.dto.AuthenticationResponse;
import ru.swap.dto.LoginRequest;
import ru.swap.dto.RegisterRequest;
import ru.swap.dto.UpdateContactInfoRequest;
import ru.swap.exceptions.SwapApplicationException;
import ru.swap.model.NotificationEmail;
import ru.swap.model.User;
import ru.swap.model.VerificationToken;
import ru.swap.repository.UserRepository;
import ru.swap.repository.VerificationTokenRepository;
import ru.swap.security.JwtProvider;

import java.util.*;

@Service
@AllArgsConstructor
@Transactional
@Slf4j
public class AuthService {

    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final VerificationTokenRepository verificationTokenRepository;
    private final MailService mailService;
    private final AuthenticationManager authenticationManager;
    private final JwtProvider jwtProvider;
    private final Map<String, Boolean> sessionStorage = new HashMap<>();

    public void signup(RegisterRequest registerRequest) {
        User user = new User();
        user.setUsername(registerRequest.getUsername());
        user.setEmail(registerRequest.getEmail());
        user.setPassword(passwordEncoder.encode(registerRequest.getPassword()));
        user.setContactInfo(registerRequest.getContactInfo());
        user.setLastName(registerRequest.getLastName());
        user.setFirstName(registerRequest.getFirstName());
        user.setCity(registerRequest.getCity());

        user.setEnabled(false);

        userRepository.save(user);

        String token = generateVerificationToken(user);
        mailService.sendMail(new NotificationEmail("Please Activate your Account",
                user.getEmail(), "Thank you for signing up to Swap, " +
                "please click on the below url to activate your account : " +
                "http://localhost:8080/api/auth/accountVerification/" + token));
    }

    @Transactional(readOnly = true)
    public User getCurrentUser() {
        Jwt principal = (Jwt) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return userRepository
                .findByUsername(principal.getSubject())
                .orElseThrow(() -> new UsernameNotFoundException("Username not found - " + principal.getSubject()));
    }

    private void fetchUserAndEnable(VerificationToken verificationToken) {
        String username = verificationToken.getUser().getUsername();
        User user = userRepository
                .findByUsername(username)
                .orElseThrow(() -> new SwapApplicationException("User not found with username - " + username));
        user.setEnabled(true);
        userRepository.save(user);
    }

    private String generateVerificationToken(User user) {
        String token = UUID.randomUUID().toString();
        VerificationToken verificationToken = new VerificationToken();
        verificationToken.setToken(token);
        verificationToken.setUser(user);

        verificationTokenRepository.save(verificationToken);
        return token;
    }

    public void verifyAccount(String token) {
        Optional<VerificationToken> verificationToken = verificationTokenRepository.findByToken(token);
        fetchUserAndEnable(verificationToken.orElseThrow(() -> new SwapApplicationException("Invalid Token")));
    }

    public synchronized boolean isSessionActive(String username) {
        return sessionStorage.getOrDefault(username, false);
    }
    public synchronized void activateSession(String username) {
        sessionStorage.put(username, true);
    }
    public synchronized void deactivateSession(String username) {
        sessionStorage.put(username, false);
    }

    public AuthenticationResponse login(LoginRequest loginRequest) {
        if (!isSessionActive(loginRequest.getUsername())) {
            Authentication authenticate =
                    authenticationManager.authenticate(
                            new UsernamePasswordAuthenticationToken(
                                    loginRequest.getUsername(),
                                    loginRequest.getPassword()));
            SecurityContextHolder.getContext().setAuthentication(authenticate);
            String token = jwtProvider.generateToken(authenticate);
            activateSession(loginRequest.getUsername());
            return AuthenticationResponse.builder()
                    .authenticationToken(token)
                    .username(loginRequest.getUsername())
                    .build();
        }

        throw new SwapApplicationException("User already logged in");
    }

    public void logout() {
        String username = ((Jwt) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getSubject();
        if (!isSessionActive(username)) deactivateSession(username);
        else throw new SwapApplicationException("User not logged in");
    }

    public void update(UpdateContactInfoRequest request) {
        Jwt principal = (Jwt) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User currentUser = userRepository
                .findByUsername(principal.getSubject())
                .orElseThrow(() -> new UsernameNotFoundException("Username not found - " + principal.getSubject()));
        if (isSessionActive(currentUser.getUsername())) {
            if (request.getCity() != null) currentUser.setCity(request.getCity());
            if (request.getContactInfo() != null) currentUser.setContactInfo(request.getContactInfo());
            userRepository.save(currentUser);
        }
    }
}