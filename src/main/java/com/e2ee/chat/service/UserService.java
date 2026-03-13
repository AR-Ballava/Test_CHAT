package com.e2ee.chat.service;
import com.e2ee.chat.entity.User;
import com.e2ee.chat.repository.UserRepo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {

    private final UserRepo userRepo;

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
