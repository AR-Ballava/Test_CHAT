package com.e2ee.chat.service;

import com.e2ee.chat.entity.Contact;
import com.e2ee.chat.repository.ContactRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ContactService {

    private final ContactRepo contactRepo;

    public Contact addContact(String owner,String contactUser){

        if(contactRepo.existsByOwnerIdAndContactUserId(owner,contactUser))
            throw new RuntimeException("Contact already exists");

        Contact contact = new Contact();
        contact.setOwnerId(owner);
        contact.setContactUserId(contactUser);
        contact.setCreatedAt(Instant.now());

        return contactRepo.save(contact);
    }

    public List<Contact> getContacts(String owner){
        return contactRepo.findByOwnerId(owner);
    }

}