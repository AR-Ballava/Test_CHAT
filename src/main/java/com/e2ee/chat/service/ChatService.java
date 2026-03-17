package com.e2ee.chat.service;

import com.e2ee.chat.dto.MessagePreview;
import com.e2ee.chat.entity.Contact;
import com.e2ee.chat.entity.Conversation;
import com.e2ee.chat.entity.Message;
import com.e2ee.chat.enums.MessageStatus;
import com.e2ee.chat.repository.ContactRepo;
import com.e2ee.chat.repository.ConversationRepo;
import com.e2ee.chat.repository.MessageRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class ChatService {

    private final MessageRepo messageRepo;
    private final ContactRepo contactRepo;
    private final ConversationRepo conversationRepo;

    public Message saveMessage(String senderId, String receiverId, String content){

        String conversationId = generateConversationId(senderId, receiverId);

        Conversation conversation = conversationRepo.findById(conversationId).orElseGet(() -> {
            Conversation newConv = new Conversation();
            newConv.setId(conversationId);
            newConv.setParticipants(Arrays.asList(senderId, receiverId));
            newConv.setCreatedAt(Instant.now());
            newConv.setUpdatedAt(Instant.now());
            return conversationRepo.save(newConv);
        });

        Message message = new Message();
        message.setConversationId(conversationId);
        message.setSenderId(senderId);
        message.setReceiverId(receiverId);
        message.setContent(content);
        message.setStatus(MessageStatus.SENT);
        message.setSentAt(Instant.now());

        conversation.setUpdatedAt(Instant.now());
        conversationRepo.save(conversation);

        return messageRepo.save(message);
    }

    public Map<String, MessagePreview> getLastMessages(String currentUser){

        Map<String, MessagePreview> previewMap = new HashMap<>();

        List<Message> messages = messageRepo.findBySenderIdOrReceiverId(currentUser, currentUser);

        for(Message msg : messages){

            String otherUser;

            if(msg.getSenderId().equals(currentUser)){
                otherUser = msg.getReceiverId();
            }else if(msg.getReceiverId().equals(currentUser)){
                otherUser = msg.getSenderId();
            }else{
                continue;
            }

            MessagePreview preview = previewMap.get(otherUser);

            if(preview == null || msg.getSentAt().isAfter(preview.sentAt())){

                long unread = messageRepo.countBySenderIdAndReceiverIdAndReadAtIsNull(otherUser,currentUser);
                MessagePreview p = new MessagePreview(msg.getSenderId(),msg.getReceiverId(),msg.getContent(), msg.getSentAt(), msg.getStatus(), unread);

                previewMap.put(otherUser, p);
            }
        }

        return previewMap;
    }

    private String generateConversationId(String user1, String user2) {
        return user1.compareTo(user2) < 0 ? user1 + "_" + user2 : user2 + "_" + user1;
    }
}
