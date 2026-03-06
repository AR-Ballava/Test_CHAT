package com.e2ee.chat.controller;

import com.e2ee.chat.dto.UserDto;
import com.e2ee.chat.entity.User;
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

    @PostMapping
    public ResponseEntity<User> createUser(@RequestBody UserDto userDto){
        return userService.createUser(userDto);
    }

    @GetMapping
    public ResponseEntity<List<User>> getAllUser(){
        return userService.findAll();
    }

    @GetMapping("/find")
    public ResponseEntity<?> findUserByEmail(@RequestParam String email){
        return userService.findUserByEmail(email);
    }
}
