package com.bookstore.controller;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.bookstore.model.Book;
import com.bookstore.model.CartItem;
import com.bookstore.model.User;
import com.bookstore.service.BookService;
import com.bookstore.service.CartService;
import com.bookstore.service.UserService;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/cart")
public class CartController 
{

    private final CartService cartService;
    private final BookService bookService;
    private final UserService userService;

    public CartController(CartService cartService, BookService bookService, UserService userService) 
    {
        this.cartService = cartService;
        this.bookService = bookService;
        this.userService = userService;
    }

    @GetMapping
    public String viewCart(HttpSession session, Model model) 
    {
        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) {
            return "redirect:/login";
        }
        List<CartItem> cart = cartService.getCart(userId);
        model.addAttribute("cart", cart);
        return "cart";
    }

    @GetMapping("/add")
    public String addToCart(@RequestParam Long bookId,
                            @RequestParam(defaultValue = "1") int qty,
                            HttpSession session) 
    {

        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) return "redirect:/login";

        User user = userService.findById(userId);
        Book book = bookService.findById(bookId);

        if (user != null && book != null)
        {
            cartService.addToCart(user, book, qty);
        }

        return "redirect:/cart";
    }

    @GetMapping("/remove/{id}")
    public String remove(@PathVariable Long id, HttpSession session) 
    {
        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) return "redirect:/login";

        CartItem item = cartService.getById(id);

        if (item != null && item.getUser() != null && item.getUser().getId().equals(userId)) 
        {
            cartService.removeFromCart(id);
        }

        return "redirect:/cart";
    }
}
