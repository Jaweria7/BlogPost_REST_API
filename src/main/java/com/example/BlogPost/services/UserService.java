package com.example.BlogPost.services;

import com.example.BlogPost.entities.User;
import com.example.BlogPost.repositories.UserRepository;
import org.springframework.stereotype.Service;

@Service
public class UserService {
     private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public void registerNewUser(User user) {
        userRepository.save(user);
    }

    public boolean usernameExists(String username) {
        return userRepository.findByUsername(username) != null;
    }
}
