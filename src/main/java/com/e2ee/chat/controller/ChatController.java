package com.e2ee.chat.controller;

import com.e2ee.chat.entity.Message;
import com.e2ee.chat.enums.MessageStatus;
import com.e2ee.chat.service.ChatService;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;

import java.security.Principal;

@Controller
@RequiredArgsConstructor
public class ChatController {

    private final ChatService chatService;
    private final SimpMessagingTemplate messagingTemplate;

//    @MessageMapping("/chat.send")
//    public void sendMessage(@Payload Message message){
//        Message savedMessage = chatService.saveMessage(message.getSenderId(), message.getReceiverId(), message.getContent());
//        messagingTemplate.convertAndSendToUser(savedMessage.getReceiverId(), "/queue/message", savedMessage);
//    }

    @MessageMapping("/chat.send")
    public void sendMessage(@Payload Message message, Principal principal) {

        System.out.println("Sender Principal: " + principal.getName());
        System.out.println("Receiver: " + message.getReceiverId());

        String sender = principal.getName();
        Message saved = chatService.saveMessage(sender, message.getReceiverId(), message.getContent());

        // send to receiver
        messagingTemplate.convertAndSendToUser(
                saved.getReceiverId(),
                "/queue/messages",
                saved
        );

        // send to sender
        messagingTemplate.convertAndSendToUser(
                sender,
                "/queue/messages",
                saved
        );
//        saved.setStatus(MessageStatus.DELIVERED);
//        messagingTemplate.convertAndSendToUser(sender, "/queue/delivery", saved);
    }

    @MessageMapping("/chat.read")
    public void readMessage(@Payload Message message){
        message.setStatus(MessageStatus.READ);
        messagingTemplate.convertAndSendToUser(message.getSenderId(), "/queue/read-receipt", message);
    }
}
