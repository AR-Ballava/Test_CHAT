package com.e2ee.chat.dto;

import com.e2ee.chat.enums.MessageStatus;
import lombok.NoArgsConstructor;

import java.time.Instant;

public record MessagePreview (String senderId, String receiverId, String content, Instant sentAt, MessageStatus status, long unreadCount) {
}
