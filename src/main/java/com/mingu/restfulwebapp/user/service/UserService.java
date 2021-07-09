package com.mingu.restfulwebapp.user.service;

import com.mingu.restfulwebapp.user.User;
import com.mingu.restfulwebapp.user.persistence.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public Optional<User> getUserById(Long id) {
        return null;
    }

    public void removeUserById(Long id) {
    }

    public User create(User user) {
        return null;
    }
}
