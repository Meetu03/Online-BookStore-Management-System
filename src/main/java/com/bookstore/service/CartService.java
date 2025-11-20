package com.bookstore.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.bookstore.model.Book;
import com.bookstore.model.CartItem;
import com.bookstore.model.User;
import com.bookstore.repository.CartRepository;

@Service
public class CartService {

    private final CartRepository repo;

    public CartService(CartRepository repo) {
        this.repo = repo;
    }

    public List<CartItem> getCart(Long userId) {
        return repo.findByUserId(userId);
    }

    public CartItem addToCart(User user, Book book, int quantity) {
        CartItem item = new CartItem();
        item.setUser(user);
        item.setBook(book);
        item.setQuantity(quantity);
        return repo.save(item);
    }

    public void removeFromCart(Long id) {
        repo.deleteById(id);
    }

    public void clearCart(Long userId) {
        repo.deleteAll(repo.findByUserId(userId));
    }

    public CartItem getById(Long id) {
        return repo.findById(id).orElse(null);
    }
}
