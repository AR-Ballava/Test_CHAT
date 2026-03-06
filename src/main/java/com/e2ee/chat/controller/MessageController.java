package com.e2ee.chat.controller;

import com.e2ee.chat.entity.Message;
import com.e2ee.chat.repository.MessageRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/messages")
@RequiredArgsConstructor
public class MessageController {

    private final MessageRepo messageRepo;

    @GetMapping("/{conversationId}")
    public ResponseEntity<List<Message>> getMessages(@PathVariable String conversationId) {
        return new ResponseEntity<>( messageRepo.findByConversationIdOrderBySentAtAsc(conversationId), HttpStatus.OK);
    }
}
