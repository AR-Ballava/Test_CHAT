package com.e2ee.chat.repository;

import com.e2ee.chat.entity.Conversation;
import com.e2ee.chat.entity.User;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ConversationRepo extends MongoRepository<Conversation, String> {
    List<Conversation> findByParticipantsContains(String userId);
}
