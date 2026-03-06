package com.e2ee.chat.service;

import com.e2ee.chat.dto.LoginDto;
import com.e2ee.chat.dto.UserDto;
import com.e2ee.chat.entity.User;
import com.e2ee.chat.repository.UserRepo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {

    private final UserRepo userRepo;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authManager;

    public ResponseEntity<User> createUser(UserDto userDto){
        if(userRepo.findByEmail(userDto.email()) != null) return new ResponseEntity<>(new User(), HttpStatus.BAD_REQUEST);

        User user = new User();
        user.setUsername(userDto.username());
        user.setEmail(userDto.email());
        user.setPassword(passwordEncoder.encode(userDto.password()));
        user.setOnline(false);
        user.setLastSeen(Instant.now());
        return new ResponseEntity<>(userRepo.save(user), HttpStatus.CREATED);
    }

    public String login(LoginDto user) {

        try {
            Authentication authentication = authManager.authenticate(
                    new UsernamePasswordAuthenticationToken(user.email(), user.password()));
            log.info("Trying to login as user : {} ", user.email());
            return jwtService.generateToken(authentication.getName());

        } catch (Exception e) {
            throw new RuntimeException("Invalid email or password for user : " + user.email());
        }
    }

    public ResponseEntity<List<User>> findAll() {
        return new ResponseEntity<>(userRepo.findAll(), HttpStatus.OK);
    }

    public ResponseEntity<?> findUserByEmail(String email) {
        User user = userRepo.findByEmail(email);
        if(user == null) {
            log.info("user not found with email : {}", email);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User Not Found");
        }
        log.info("user found with email : {}", email);
        return ResponseEntity.ok(user);
    }
}
