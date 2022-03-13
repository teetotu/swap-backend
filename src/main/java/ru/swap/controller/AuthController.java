package ru.swap.controller;

import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.swap.dto.AuthenticationResponse;
import ru.swap.dto.LoginRequest;
import ru.swap.dto.RegisterRequest;
import ru.swap.dto.UpdateContactInfoRequest;
import ru.swap.exceptions.SwapApplicationException;
import ru.swap.service.AuthService;

import static org.springframework.http.HttpStatus.*;

@RestController
@RequestMapping("/api/auth")
@AllArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/signup")
    public ResponseEntity<String> signup(@RequestBody RegisterRequest registerRequest) {
        authService.signup(registerRequest);
        return new ResponseEntity<>("User Registration Successful", OK);
    }

    @GetMapping("accountVerification/{token}")
    public ResponseEntity<String> verifyAccount(@PathVariable String token) {
        authService.verifyAccount(token);
        return new ResponseEntity<>("Account Activated Successfully", OK);
    }

    @PostMapping("/login")
    public AuthenticationResponse login(@RequestBody LoginRequest loginRequest) {
        return authService.login(loginRequest);
    }

    @PostMapping("/logout")
    public ResponseEntity<String> logout() {
        try {
            authService.logout();
        } catch (SwapApplicationException e) {
            return ResponseEntity.status(NOT_FOUND).body(e.getMessage());
        }
        return ResponseEntity.status(OK).body("Refresh Token Deleted Successfully");
    }

    @PutMapping
    public ResponseEntity<String> update(@RequestBody UpdateContactInfoRequest request) {
        authService.update(request);
        return ResponseEntity.status(NO_CONTENT).body("updated user info");
    }
}
