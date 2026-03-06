package com.e2ee.chat.controller;

import com.e2ee.chat.dto.AuthResponse;
import com.e2ee.chat.dto.LoginDto;
import com.e2ee.chat.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;

    @PostMapping("/login")
    public ResponseEntity<?> getStudent(@RequestBody LoginDto user){
        String jwtToken = userService.login(user);
        if(jwtToken != null) {
            return ResponseEntity.ok(new AuthResponse(jwtToken));
        }
        else return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Login failed bad credential");
    }

}
