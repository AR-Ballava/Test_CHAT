package com.e2ee.chat.controller;

import com.e2ee.chat.entity.Conversation;
import com.e2ee.chat.repository.ConversationRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/conversations")
@RequiredArgsConstructor
public class ConversationController {

    private final ConversationRepo conversationRepo;

    @GetMapping("/{userId}")
    public ResponseEntity<List<Conversation>> getUserConversations(@PathVariable String userId){
        return new ResponseEntity<>(conversationRepo.findByParticipantsContains(userId), HttpStatus.OK);
    }
}
