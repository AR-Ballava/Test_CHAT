package com.e2ee.chat.service;

import com.e2ee.chat.entity.Contact;
import com.e2ee.chat.entity.User;
import com.e2ee.chat.repository.ContactRepo;
import com.e2ee.chat.repository.UserRepo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ContactService {

    private final ContactRepo contactRepo;
    private final UserRepo userRepo;

    // ✅ ADD CONTACT (UNCHANGED)
    public ResponseEntity<?> addContact(String owner, String contactUser){

        if(contactRepo.existsByOwnerIdAndContactUserId(owner, contactUser)){
            return ResponseEntity.badRequest().body("Contact already exists");
        }

        User user = userRepo.findByEmail(contactUser);
        if(user == null){
            return ResponseEntity.notFound().build();
        }

        Contact contact1 = new Contact();
        contact1.setOwnerId(owner);
        contact1.setContactUserId(contactUser);
        contact1.setCreatedAt(Instant.now());

        contactRepo.save(contact1);

        if(!contactRepo.existsByOwnerIdAndContactUserId(contactUser, owner)){
            Contact contact2 = new Contact();
            contact2.setOwnerId(contactUser);
            contact2.setContactUserId(owner);
            contact2.setCreatedAt(Instant.now());
            contactRepo.save(contact2);
        }

        return ResponseEntity.ok(contact1);
    }

    // 🔥 SEARCH USERS (FINAL)
    public List<User> searchUsers(String keyword, String currentUser) {

        String regex = "^" + keyword;   // 🔥 FIX

        Pageable pageable = PageRequest.of(0, 10);

        List<User> users = userRepo.searchUsers(regex, pageable);

        List<Contact> contacts = contactRepo.findByOwnerId(currentUser);

        Set<String> existingContacts = contacts.stream()
                .map(Contact::getContactUserId)
                .collect(Collectors.toSet());

        return users.stream()
                .filter(u -> !u.getEmail().equals(currentUser))
                .filter(u -> !existingContacts.contains(u.getEmail()))
                .sorted((a, b) -> {
                    String k = keyword.toLowerCase();

                    boolean aStarts = a.getEmail().toLowerCase().startsWith(k)
                            || a.getUsername().toLowerCase().startsWith(k);

                    boolean bStarts = b.getEmail().toLowerCase().startsWith(k)
                            || b.getUsername().toLowerCase().startsWith(k);

                    if (aStarts && !bStarts) return -1;
                    if (!aStarts && bStarts) return 1;
                    return 0;
                })
                .toList();
    }

    public List<Contact> getContacts(String owner){
        return contactRepo.findByOwnerId(owner);
    }
}