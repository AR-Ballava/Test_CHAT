package com.e2ee.chat.controller;

import com.e2ee.chat.entity.Contact;
import com.e2ee.chat.entity.User;
import com.e2ee.chat.repository.UserRepo;
import com.e2ee.chat.service.ContactService;
import com.e2ee.chat.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/contacts")
@RequiredArgsConstructor
public class ContactController {

    private final ContactService contactService;

    @PostMapping("/{contactUser}")
    public ResponseEntity<?> addContact(@PathVariable String contactUser, Authentication auth){
        String owner = auth.getName();
        return contactService.addContact(owner,contactUser);
    }

    @GetMapping
    public List<Contact> getContacts(Authentication auth){
        return contactService.getContacts(auth.getName());
    }

    @GetMapping("/search")
    public ResponseEntity<List<User>> searchUsers(
            @RequestParam String keyword,
            Authentication auth
    ) {
        String currentUser = auth.getName();
        return ResponseEntity.ok(contactService.searchUsers(keyword, currentUser));
    }

}