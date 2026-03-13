package com.e2ee.chat.service;

import com.e2ee.chat.entity.Contact;
import com.e2ee.chat.entity.User;
import com.e2ee.chat.repository.ContactRepo;
import com.e2ee.chat.repository.UserRepo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ContactService {

    private final ContactRepo contactRepo;
    private final UserRepo userRepo;

    public ResponseEntity<?> addContact(String owner,String contactUser){

        if(contactRepo.existsByOwnerIdAndContactUserId(owner,contactUser)){
            log.info("contact already present in contact list {}", contactUser);
            return ResponseEntity.badRequest().build();
        }
        User user = userRepo.findByEmail(contactUser);
        if(user == null){
            log.info("Contact is not a register chat user {}", contactUser);
            return ResponseEntity.notFound().build();
        }

        Contact contact = new Contact();
        contact.setOwnerId(owner);
        contact.setContactUserId(contactUser);
        contact.setCreatedAt(Instant.now());
        log.info("{} contact added successfully for {} ", contactUser, owner);
        return ResponseEntity.ok(contactRepo.save(contact));
    }

    public List<Contact> getContacts(String owner){
        return contactRepo.findByOwnerId(owner);
    }

}