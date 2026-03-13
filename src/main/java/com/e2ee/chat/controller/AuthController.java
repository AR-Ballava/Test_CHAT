package com.e2ee.chat.controller;

import com.e2ee.chat.dto.AuthResponse;
import com.e2ee.chat.dto.LoginDto;
import com.e2ee.chat.dto.RegisterDto;
import com.e2ee.chat.entity.User;
import com.e2ee.chat.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<User> registerUser(@RequestBody RegisterDto registerDto){
        return authService.createUser(registerDto);
    }

    @PostMapping("/login")
    public ResponseEntity<?> getStudent(@RequestBody LoginDto user){
        AuthResponse tokens = authService.login(user);
        if(tokens != null) return ResponseEntity.ok(tokens);
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Login failed bad credential");
    }

    @PostMapping("/refresh")
    public ResponseEntity<?> refreshToken(@RequestBody Map<String, String> body){
        return authService.refreshToken(body.get("refreshToken"));
    }

}
