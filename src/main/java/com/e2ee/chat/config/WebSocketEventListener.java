package com.e2ee.chat.config;

import com.e2ee.chat.entity.User;
import com.e2ee.chat.repository.UserRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.stomp.*;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionConnectedEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

import java.time.Instant;

@Component
@RequiredArgsConstructor
public class WebSocketEventListener {

    private final UserRepo userRepo;

    @EventListener
    public void handleConnect(SessionConnectedEvent event) {

        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(event.getMessage());
        Authentication auth = (Authentication) accessor.getUser();

        if(auth != null){
            String email = auth.getName();
            User user = userRepo.findByEmail(email);
            user.setOnline(true);
            userRepo.save(user);
        }
    }

    @EventListener
    public void handleDisconnect(SessionDisconnectEvent event){

        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(event.getMessage());
        Authentication auth = (Authentication) accessor.getUser();

        if(auth != null){
            String email = auth.getName();
            User user = userRepo.findByEmail(email);
            user.setOnline(false);
            user.setLastSeen(Instant.now());
            userRepo.save(user);
        }
    }
}