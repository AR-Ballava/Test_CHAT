package com.e2ee.chat.repository;

import com.e2ee.chat.entity.User;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserRepo extends MongoRepository<User, String> {
    User findByEmail(String email);

    List<User> findTop10ByUsernameContainingIgnoreCaseOrEmailContainingIgnoreCase(String username, String email);
}
