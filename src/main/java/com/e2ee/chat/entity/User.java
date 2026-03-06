package com.e2ee.chat.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

@Document(collection = "users")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class User {
    @Id
    private String Id;

    private String username;
    private String email;
    private String password;

    private String bio;
    private String profilePicture;

    private boolean online;
    private Instant lastSeen;

    private Instant createdAt;
}
