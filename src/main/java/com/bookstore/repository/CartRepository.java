package com.bookstore.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.bookstore.model.CartItem;

public interface CartRepository extends JpaRepository<CartItem, Long> 
{
    List<CartItem> findByUserId(Long userId); // Spring Data supports nested property
}
