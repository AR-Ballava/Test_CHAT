package com.e2ee.chat.entity;

import com.e2ee.chat.enums.MessageStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

@Document(collection = "messages")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Message {
    @Id
    private String id;

    private String conversationId;
    private String senderId;
    private String receiverId;
    private String content;
    private MessageStatus status; // SENT, DELIVERED, READ
    private Instant sentAt;
    private Instant deliveredAt;
    private Instant readAt;
}
