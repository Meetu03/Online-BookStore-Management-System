package com.bookstore.service;

import org.springframework.stereotype.Service;
import com.bookstore.model.CartItem;
import com.bookstore.model.Order;
import com.bookstore.model.User;
import com.bookstore.repository.CartRepository;
import com.bookstore.repository.OrderRepository;

import java.math.BigDecimal;
import java.util.List;

@Service
public class OrderService {

    private final OrderRepository orderRepo;
    private final CartRepository cartRepo;

    public OrderService(OrderRepository orderRepo, CartRepository cartRepo) {
        this.orderRepo = orderRepo;
        this.cartRepo = cartRepo;
    }

    public Order placeOrder(User user, List<CartItem> items) {
        BigDecimal total = items.stream()
                .map(i -> {
                    BigDecimal price = i.getBook().getPrice() == null ? BigDecimal.ZERO : i.getBook().getPrice();
                    return price.multiply(BigDecimal.valueOf(i.getQuantity()));
                })
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        Order order = new Order();
        order.setUser(user);
        order.setItems(items);
        order.setTotalAmount(total);
        order.setPaymentStatus("Paid (Mock)");

        Order saved = orderRepo.save(order);
        cartRepo.deleteAll(items);
        return saved;
    }
    
    public List<Order> getOrdersByUser(Long userId) {
        return orderRepo.findByUserId(userId);
    }
}
