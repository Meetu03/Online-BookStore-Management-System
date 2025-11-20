package com.bookstore.controller;

import com.bookstore.model.CartItem;
import com.bookstore.model.Order;
import com.bookstore.model.User;
import com.bookstore.service.CartService;
import com.bookstore.service.OrderService;
import com.bookstore.service.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/orders")
public class OrderController {

    private final OrderService orderService;
    private final CartService cartService;
    private final UserService userService;

    public OrderController(OrderService orderService, CartService cartService, UserService userService) {
        this.orderService = orderService;
        this.cartService = cartService;
        this.userService = userService;
    }

    @GetMapping
    public String viewOrders(HttpSession session, Model model) {
        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) return "redirect:/login";
        List<Order> orders = orderService.getOrdersByUser(userId);
        model.addAttribute("orders", orders);
        return "orders";
    }

    @GetMapping("/place")
    public String placeOrder(HttpSession session) {
        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) return "redirect:/login";
        User user = userService.findById(userId);
        List<CartItem> items = cartService.getCart(userId);
        if (items == null || items.isEmpty()) return "redirect:/cart";
        orderService.placeOrder(user, items);
        return "redirect:/orders";
    }
}