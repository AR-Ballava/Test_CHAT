package com.e2ee.chat.dto;

public record VerifyOtpRequest(String email, String username, String password, String otp) {
}
