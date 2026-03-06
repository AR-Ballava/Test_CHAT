package com.e2ee.chat.entity;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "contacts")
public class Contact {

    @Id
    private String id;

    private String ownerId;
    private String contactUserId;

    private Instant createdAt;
}