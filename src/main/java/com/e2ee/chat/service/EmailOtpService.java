package com.e2ee.chat.service;

import com.e2ee.chat.dto.*;
import com.e2ee.chat.entity.EmailOtp;
import com.e2ee.chat.entity.User;
import com.e2ee.chat.repository.EmailOtpRepository;
import com.e2ee.chat.repository.UserRepo;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.Duration;
import java.time.Instant;
import java.util.Optional;
import java.util.Random;

@Service
@Slf4j
@RequiredArgsConstructor
public class EmailOtpService {

    private static final SecureRandom random = new SecureRandom();
    private final JavaMailSender mailSender;
    private final EmailOtpRepository emailOtpRepo;
    private final AuthService authService;
    private final UserRepo userRepo;
    private final PasswordEncoder passwordEncoder;
    private static final long OTP_LIMIT_WINDOW = 12 * 60 * 60 * 1000L;

    public void sendOtp(String email, String otp) {

        try {

            LocalDateTime now = LocalDateTime.now();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd MMM yyyy, HH:mm");
            String dateTime = now.format(formatter);

            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);

            helper.setTo(email);
            helper.setSubject("Your Verification Code " + otp);

            String htmlContent = """
                <!DOCTYPE html>
                <html>
                <body style="margin:0;padding:0;background:#f4f6fb;font-family:Arial,Helvetica,sans-serif;">
                
                <table width="100%" cellpadding="0" cellspacing="0" style="padding:20px 10px;">
                <tr>
                <td align="center">
                
                <table width="100%" cellpadding="0" cellspacing="0" 
                style="max-width:500px;background:#ffffff;border-radius:12px;
                box-shadow:0 4px 18px rgba(0,0,0,0.08);padding:30px;">
                
                <tr>
                <td align="center">
                
                <h2 style="margin:0;color:#222;">Verify Your Email</h2>
                
                <p style="color:#555;font-size:15px;margin-top:15px;">
                Use the verification code below to continue.
                </p>
                
                <div style="margin:30px 0;">
                <span style="
                display:inline-block;
                background:#111;
                color:#fff;
                font-size:32px;
                font-weight:bold;
                letter-spacing:6px;
                padding:14px 28px;
                border-radius:8px;">
                """ + otp + """
                </span>
                </div>
                
                <p style="color:#666;font-size:14px;">
                This code will expire in <b>5 minutes</b>.
                </p>
                
                <p style="color:#888;font-size:13px;margin-top:25px;">
                Generated on : """ + dateTime + """
                </p>
                
                <hr style="border:none;border-top:1px solid #eee;margin:25px 0;">
                
                <p style="color:#999;font-size:12px;">
                If you didn't request this email, you can safely ignore it.
                </p>
                
                <p style="color:#bbb;font-size:11px;margin-top:20px;">
                © 2026 Ar-Chat
                </p>
                
                </td>
                </tr>
                </table>
                
                </td>
                </tr>
                </table>
                
                </body>
                </html>
                """;

            helper.setText(htmlContent, true);
            mailSender.send(message);

        } catch (Exception e) {
            throw new RuntimeException("Failed to send OTP email", e);
        }
    }

    public ResponseEntity<?> sendOtp(RegisterRequest request){

        if(userRepo.existsByEmail(request.email())){
            return ResponseEntity.badRequest().body("Email already registered");
        }

        Optional<EmailOtp> existing = emailOtpRepo.findByEmail(request.email());
        long now = System.currentTimeMillis();

        if(existing.isPresent()){

            EmailOtp doc = existing.get();

            if(doc.getRequestCount() >= 3 && (now - doc.getCreatedAt()) < OTP_LIMIT_WINDOW){
                return ResponseEntity.badRequest().body("Maximum 3 OTP requests allowed in 12 hours");
            }

        }

        String otp = generateOtp();
        String hashedOtp = passwordEncoder.encode(otp);

        EmailOtp otpDoc = existing.orElse(new EmailOtp());

        otpDoc.setEmail(request.email());
        otpDoc.setOtp(hashedOtp);
        otpDoc.setCreatedAt(now);
        otpDoc.setExpiresAt(Instant.now().plus(Duration.ofMinutes(5)));

        int count = 0;
        if(existing.isPresent()){
            EmailOtp doc = existing.get();
            if(now - doc.getCreatedAt() < 43200000) count = doc.getRequestCount();
        }
        otpDoc.setRequestCount(count + 1);

        emailOtpRepo.save(otpDoc);
        sendOtp(request.email(), otp);
        log.info("Otp send for email: {}", request.email());


        return ResponseEntity.ok("OTP sent successfully");
    }


    public ResponseEntity<?> verifyOtp(VerifyOtpRequest request){
        Optional<EmailOtp> otpOptional = emailOtpRepo.findByEmail(request.email());

        if(otpOptional.isEmpty()) return ResponseEntity.badRequest().body("OTP not found");

        EmailOtp otpDoc = otpOptional.get();
        long now = System.currentTimeMillis();

        if (Instant.now().isAfter(otpDoc.getExpiresAt())) {
            log.info("Otp Expired");
            return ResponseEntity.badRequest().body("OTP expired");
        }
        if(!passwordEncoder.matches(request.otp(), otpDoc.getOtp())) {
            log.info("Invalid Otp");
            return ResponseEntity.badRequest().body("Invalid OTP");
        }


        RegisterDto registerDto = new RegisterDto(request.username(), request.email(), request.password());
        ResponseEntity<User> response = authService.createUser(registerDto);
        emailOtpRepo.deleteByEmail(request.email());

        return response;
    }

    public ResponseEntity<?> resendOtp(EmailRequest request){

        Optional<EmailOtp> otpOptional = emailOtpRepo.findByEmail(request.email());
        if(otpOptional.isEmpty()) return ResponseEntity.badRequest().body("OTP not requested");
        EmailOtp doc = otpOptional.get();
        long now = System.currentTimeMillis();

        if(now - doc.getCreatedAt() < 30000){
            return ResponseEntity.badRequest().body("Please wait before requesting new OTP");
        }

        String otp = generateOtp();
        String hashedOtp = passwordEncoder.encode(otp);

        doc.setOtp(hashedOtp);
        doc.setCreatedAt(now);
        doc.setExpiresAt(Instant.now().plus(Duration.ofMinutes(5)));
        doc.setRequestCount(doc.getRequestCount()+1);

        emailOtpRepo.save(doc);
        log.info("Otp Re-send for email : {}", request.email());
        sendOtp(request.email(), otp);

        return ResponseEntity.ok("OTP resent");
    }

    public ResponseEntity<?> sendForgotPasswordOtp(ForgotPasswordRequest request){

        User user = userRepo.findByEmail(request.email());

        if(user == null){
            log.info("email not registered {}", request.email());
            return ResponseEntity.badRequest().body("Email not registered");
        }

        String otp = generateOtp();
        String hashedOtp = passwordEncoder.encode(otp);

        EmailOtp otpDoc = new EmailOtp();

        otpDoc.setEmail(request.email());
        otpDoc.setOtp(hashedOtp);
        otpDoc.setCreatedAt(System.currentTimeMillis());
        otpDoc.setExpiresAt(Instant.now().plus(Duration.ofMinutes(5)));
        otpDoc.setRequestCount(1);

        emailOtpRepo.save(otpDoc);

        sendOtp(request.email(), otp);
        log.info("Otp send for email : {}", request.email());


        return ResponseEntity.ok("OTP sent for password reset");
    }

    public ResponseEntity<?> resetPassword(ResetPasswordRequest request){

        Optional<EmailOtp> otpOptional = emailOtpRepo.findByEmail(request.email());

        if(otpOptional.isEmpty()){
            return ResponseEntity.badRequest().body("OTP not found");
        }

        EmailOtp otpDoc = otpOptional.get();

        if(Instant.now().isAfter(otpDoc.getExpiresAt())){
            return ResponseEntity.badRequest().body("OTP expired");
        }

        if(!passwordEncoder.matches(request.otp(), otpDoc.getOtp())){
            return ResponseEntity.badRequest().body("Invalid OTP");
        }

        User user = userRepo.findByEmail(request.email());

        if(user == null){
            return ResponseEntity.badRequest().body("User not found");
        }

        user.setPassword(passwordEncoder.encode(request.newPassword()));
        userRepo.save(user);

        emailOtpRepo.deleteByEmail(request.email());

        return ResponseEntity.ok("Password reset successful");
    }

    public String generateOtp(){
        int otp = 100000 + random.nextInt(900000);
        return String.valueOf(otp);
    }
}
