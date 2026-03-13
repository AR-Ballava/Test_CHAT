package com.e2ee.chat.repository;

import com.e2ee.chat.entity.Contact;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface ContactRepo extends MongoRepository<Contact,String> {

    List<Contact> findByOwnerId(String ownerId);

    boolean existsByOwnerIdAndContactUserId(String ownerId,String contactUserId);

}