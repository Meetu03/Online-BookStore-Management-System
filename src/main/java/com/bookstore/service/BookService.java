package com.bookstore.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.bookstore.model.Book;
import com.bookstore.repository.BookRepository;

@Service
public class BookService {

    private final BookRepository repo;

    public BookService(BookRepository repo) {
        this.repo = repo;
    }

    public List<Book> getAll() {
        return repo.findAll();
    }

    public Book addBook(Book book) {
        return repo.save(book);
    }

    public List<Book> search(String q) {
        return repo.findByTitleContainingIgnoreCase(q);
    }

    public Book findById(Long id) {
        return repo.findById(id).orElse(null);
    }
}
