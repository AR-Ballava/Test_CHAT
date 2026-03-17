package com.e2ee.chat.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

@Document(collection = "email_otps")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class EmailOtp {

    @Id
    private String id;

    private String email;

    private String otp;

    private long createdAt;

    @Indexed(expireAfter = "0s")
    private Instant expiresAt;

    private int requestCount;

}