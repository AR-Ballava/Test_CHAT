package com.e2ee.chat.controller;

import com.e2ee.chat.dto.MessagePreview;
import com.e2ee.chat.entity.Message;
import com.e2ee.chat.enums.MessageStatus;
import com.e2ee.chat.repository.MessageRepo;
import com.e2ee.chat.service.ChatService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/messages")
@RequiredArgsConstructor
public class MessageController {

    private final MessageRepo messageRepo;
    private final SimpMessagingTemplate messagingTemplate;
    private final ChatService chatService;

    @GetMapping("/{conversationId}")
    public ResponseEntity<List<Message>> getMessages(@PathVariable String conversationId) {
        return new ResponseEntity<>(
                messageRepo.findByConversationIdOrderBySentAtAsc(conversationId),
                HttpStatus.OK
        );
    }

    /* -------- MARK DELIVERED -------- */

    @PostMapping("/delivered/{id}")
    public void markDelivered(@PathVariable String id){

        Message msg = messageRepo.findById(id).orElseThrow();

        // prevent repeated delivered updates
        if(msg.getStatus() != MessageStatus.DELIVERED && msg.getStatus() == MessageStatus.SENT) {

            msg.setStatus(MessageStatus.DELIVERED);
            msg.setDeliveredAt(Instant.now());

            messageRepo.save(msg);

            messagingTemplate.convertAndSendToUser(
                    msg.getSenderId(),
                    "/queue/status",
                    msg
            );
        }
    }

    /* -------- MARK READ -------- */

    @PostMapping("/read/{id}")
    public void markRead(@PathVariable String id){

        Message msg = messageRepo.findById(id).orElseThrow();

        // prevent repeated read updates
        if(msg.getStatus() != MessageStatus.READ && msg.getStatus() == MessageStatus.DELIVERED){

            msg.setStatus(MessageStatus.READ);
            msg.setReadAt(Instant.now());

            messageRepo.save(msg);

            messagingTemplate.convertAndSendToUser(
                    msg.getSenderId(),
                    "/queue/status",
                    msg
            );
        }
    }

    @PostMapping("/read/conversation/{conversationId}")
    public void markConversationRead(@PathVariable String conversationId, Authentication auth){

        String currentUser = auth.getName();

        List<Message> messages = messageRepo.findByConversationIdOrderBySentAtAsc(conversationId);

        for(Message msg : messages){
            if(msg.getReceiverId().equals(currentUser) && msg.getReadAt() == null){
                msg.setStatus(MessageStatus.READ);
                msg.setReadAt(Instant.now());
            }
        }

        messageRepo.saveAll(messages);
    }

    /*...........Message Preview..............*/
    @GetMapping("/preview")
    public Map<String, MessagePreview> getChatPreview(Authentication auth){
        return chatService.getLastMessages(auth.getName());
    }
}