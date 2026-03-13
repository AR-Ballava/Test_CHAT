package com.e2ee.chat.repository;

import com.e2ee.chat.entity.Message;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MessageRepo extends MongoRepository<Message, String> {
    List<Message> findByConversationIdOrderBySentAtAsc(String conversationId);
    Message findTopByConversationIdOrderBySentAtDesc(String conversationId);
}
