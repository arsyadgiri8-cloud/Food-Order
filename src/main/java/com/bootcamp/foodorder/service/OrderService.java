package com.bootcamp.foodorder.service;

import com.bootcamp.foodorder.dto.CheckoutResponse;
import com.bootcamp.foodorder.dto.OrderItemResponse;
import com.bootcamp.foodorder.dto.OrderResponse;
import com.bootcamp.foodorder.entity.CartItem;
import com.bootcamp.foodorder.entity.Menu;
import com.bootcamp.foodorder.entity.Order;
import com.bootcamp.foodorder.entity.OrderItem;
import com.bootcamp.foodorder.entity.OrderStatus;
import com.bootcamp.foodorder.entity.User;
import com.bootcamp.foodorder.repository.CartItemRepository;
import com.bootcamp.foodorder.repository.OrderItemRepository;
import com.bootcamp.foodorder.repository.OrderRepository;
import com.bootcamp.foodorder.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
public class OrderService {

    private final CartItemRepository cartItemRepository;
    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final UserRepository userRepository;

    public OrderService(CartItemRepository cartItemRepository,
                        OrderRepository orderRepository,
                        OrderItemRepository orderItemRepository,
                        UserRepository userRepository) {
        this.cartItemRepository = cartItemRepository;
        this.orderRepository = orderRepository;
        this.orderItemRepository = orderItemRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public CheckoutResponse checkout(Authentication authentication) {
        User user = getCurrentUser(authentication);

        List<CartItem> cartItems = cartItemRepository.findByUser(user);

        if (cartItems.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Cart is empty");
        }

        for (CartItem cartItem : cartItems) {
            Menu menu = cartItem.getMenu();

            if (menu.getStock() <= 0) {
                throw new ResponseStatusException(
                        HttpStatus.BAD_REQUEST,
                        "Menu " + menu.getName() + " is out of stock"
                );
            }

            if (cartItem.getQuantity() > menu.getStock()) {
                throw new ResponseStatusException(
                        HttpStatus.BAD_REQUEST,
                        "Stock is not enough for menu " + menu.getName()
                );
            }
        }

        BigDecimal totalAmount = BigDecimal.ZERO;
        List<OrderItem> orderItems = new ArrayList<>();

        Order order = Order.builder()
                .user(user)
                .totalAmount(BigDecimal.ZERO)
                .status(OrderStatus.PAID)
                .build();

        Order savedOrder = orderRepository.save(order);

        for (CartItem cartItem : cartItems) {
            Menu menu = cartItem.getMenu();
            BigDecimal price = menu.getPrice();
            BigDecimal subtotal = price.multiply(BigDecimal.valueOf(cartItem.getQuantity()));

            OrderItem orderItem = OrderItem.builder()
                    .order(savedOrder)
                    .menu(menu)
                    .quantity(cartItem.getQuantity())
                    .price(price)
                    .subtotal(subtotal)
                    .build();

            orderItems.add(orderItem);
            totalAmount = totalAmount.add(subtotal);

            menu.setStock(menu.getStock() - cartItem.getQuantity());
        }

        orderItemRepository.saveAll(orderItems);

        savedOrder.setTotalAmount(totalAmount);
        orderRepository.save(savedOrder);

        cartItemRepository.deleteByUser(user);

        return CheckoutResponse.builder()
                .orderId(savedOrder.getId())
                .totalAmount(savedOrder.getTotalAmount())
                .status(savedOrder.getStatus().name())
                .message("Checkout success")
                .build();
    }

    public List<OrderResponse> getOrderHistory(Authentication authentication) {
        User user = getCurrentUser(authentication);

        List<Order> orders = orderRepository.findByUserOrderByCreatedAtDesc(user);

        return orders.stream()
                .map(this::mapToOrderResponse)
                .toList();
    }

    private OrderResponse mapToOrderResponse(Order order) {
        List<OrderItem> orderItems = orderItemRepository.findByOrder(order);

        List<OrderItemResponse> itemResponses = orderItems.stream()
                .map(orderItem -> OrderItemResponse.builder()
                        .menuId(orderItem.getMenu().getId())
                        .menuName(orderItem.getMenu().getName())
                        .price(orderItem.getPrice())
                        .quantity(orderItem.getQuantity())
                        .subtotal(orderItem.getSubtotal())
                        .build())
                .toList();

        return OrderResponse.builder()
                .orderId(order.getId())
                .totalAmount(order.getTotalAmount())
                .status(order.getStatus().name())
                .createdAt(order.getCreatedAt())
                .items(itemResponses)
                .build();
    }

    private User getCurrentUser(Authentication authentication) {
        String email = authentication.getName();

        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
    }
}
