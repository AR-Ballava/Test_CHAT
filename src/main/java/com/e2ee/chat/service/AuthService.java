package com.e2ee.chat.service;

import com.e2ee.chat.dto.AuthResponse;
import com.e2ee.chat.dto.LoginDto;
import com.e2ee.chat.dto.RegisterDto;
import com.e2ee.chat.entity.User;
import com.e2ee.chat.repository.UserRepo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {

    private final UserRepo userRepo;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authManager;
    private final UserDetailServiceImpl userDetailService;

    public ResponseEntity<User> createUser(RegisterDto registerDto){
        if(userRepo.findByEmail(registerDto.email()) != null) {
            log.info("user {} already exist", registerDto.email());
            return new ResponseEntity<>(new User(), HttpStatus.BAD_REQUEST);
        }

        User user = new User();
        user.setUsername(registerDto.username());
        user.setEmail(registerDto.email());
        user.setPassword(passwordEncoder.encode(registerDto.password()));
        user.setOnline(false);
        user.setLastSeen(Instant.now());
        log.info("user successfully register with email {}", registerDto.email());
        return new ResponseEntity<>(userRepo.save(user), HttpStatus.CREATED);
    }

    public AuthResponse login(LoginDto user) {

        try {
            Authentication authentication = authManager.authenticate(new UsernamePasswordAuthenticationToken(user.email(), user.password()));

            String accessToken = jwtService.generateAccessToken(user.email());
            String refreshToken = jwtService.generateRefreshToken(user.email());

            log.info("Trying to login as user : {} ", user.email());
            return new AuthResponse(accessToken, refreshToken);

        } catch (Exception e) {
            throw new RuntimeException("Invalid email or password for user : " + user.email());
        }
    }

    public ResponseEntity<?> refreshToken(String refreshToken){

        try{
            String email = jwtService.extractUserName(refreshToken);
            UserDetails user = userDetailService.loadUserByUsername(email);

            if(jwtService.validateToken(refreshToken,user)){
                String newAccessToken = jwtService.generateAccessToken(email);
                log.info("successfully refresh token used to stay log in");
                return ResponseEntity.ok(new AuthResponse(newAccessToken, refreshToken));
            }

        }catch(Exception e){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid refresh token");
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid refresh token");
    }

}
