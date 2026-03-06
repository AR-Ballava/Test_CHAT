package com.e2ee.chat.controller;

import com.e2ee.chat.dto.ProfileUpdateDto;
import com.e2ee.chat.entity.User;
import com.e2ee.chat.repository.UserRepo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@RequestMapping("/api/profile")
@RequiredArgsConstructor
@Slf4j
public class ProfileController {

    private final UserRepo userRepo;

    @GetMapping("/me")
    public User getMyProfile(Principal principal){
        log.info("getting profile of self {}", principal.getName());
        return userRepo.findByEmail(principal.getName());
    }

    @GetMapping("/{email}")
    public User getUserProfile(@PathVariable String email){
        log.info("getting profile of user {}", email);
        return userRepo.findByEmail(email);
    }

    @PutMapping("/update")
    public User updateProfile(@RequestBody ProfileUpdateDto updated, Principal principal){

        User user = userRepo.findByEmail(principal.getName());

        if(updated.username()!=null)
            user.setUsername(updated.username());

        if(updated.bio()!=null)
            user.setBio(updated.bio());

        if(updated.profilePicture()!=null)
            user.setProfilePicture(updated.profilePicture());

        return userRepo.save(user);
    }
}