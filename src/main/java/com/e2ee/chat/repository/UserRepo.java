package com.e2ee.chat.repository;

import com.e2ee.chat.entity.User;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserRepo extends MongoRepository<User, String> {
    User findByEmail(String email);
    boolean existsByEmail(String email);

    // 🔥 SEARCH BY NAME OR EMAIL (CASE INSENSITIVE)
    @Query("{ $or: [ " +
            "{ 'username': { $regex: ?0, $options: 'i' } }, " +
            "{ 'email': { $regex: ?0, $options: 'i' } } " +
            "] }")
    List<User> searchUsers(String keyword, Pageable pageable);
}
