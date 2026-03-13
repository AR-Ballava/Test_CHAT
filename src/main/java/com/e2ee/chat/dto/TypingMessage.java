package com.e2ee.chat.dto;

public record TypingMessage(String senderId, String receiverId, boolean typing) {
}
