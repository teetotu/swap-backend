package ru.swap.controller;

import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.swap.dto.UserResponse;
import ru.swap.service.UserService;

import static org.springframework.http.ResponseEntity.status;

@RestController
@RequestMapping("/api/user/")
@AllArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/{username}")
    public ResponseEntity<UserResponse> getUser(@PathVariable("username") String username) {
        return status(HttpStatus.OK).body(userService.loadUserByUsername(username));
    }
}
