package com.bookstore.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.bookstore.model.User;

public interface UserRepository extends JpaRepository<User, Long> 
{
    User findByEmail(String email);
}
