package com.e2ee.chat.repository;

import com.e2ee.chat.entity.EmailOtp;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface EmailOtpRepository extends MongoRepository<EmailOtp,String> {

    Optional<EmailOtp> findByEmail(String email);

    void deleteByEmail(String email);

}