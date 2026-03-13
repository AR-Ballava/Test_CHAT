package com.e2ee.chat.controller;

import com.e2ee.chat.entity.User;
import com.e2ee.chat.repository.UserRepo;
import com.e2ee.chat.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


import java.util.List;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final UserRepo userRepo;

    @GetMapping
    public ResponseEntity<List<User>> getAllUser(){
        return userService.findAll();
    }

    @GetMapping("/search")
    public ResponseEntity<List<User>> searchUsers(@RequestParam String query) {
        List<User> users = userRepo.findTop10ByUsernameContainingIgnoreCaseOrEmailContainingIgnoreCase(query, query);
        return ResponseEntity.ok(users);
    }

    @GetMapping("/find")
    public ResponseEntity<?> findUserByEmail(@RequestParam String email){
        return userService.findUserByEmail(email);
    }

}
