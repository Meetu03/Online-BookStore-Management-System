package com.bookstore.service;

import java.util.Optional;

import org.springframework.stereotype.Service;

import com.bookstore.model.User;
import com.bookstore.repository.UserRepository;

@Service
public class UserService {

    private final UserRepository repo;

    public UserService(UserRepository repo) {
        this.repo = repo;
    }

    public User register(User user) {
        user.setRole("USER");
        return repo.save(user);
    }

    public Optional<User> login(String email, String password) {
        User user = repo.findByEmail(email);
        if (user != null && user.getPassword().equals(password)) {
            return Optional.of(user);
        }
        return Optional.empty();
    }

    public User findById(Long id) {
        return repo.findById(id).orElse(null);
    }
}
