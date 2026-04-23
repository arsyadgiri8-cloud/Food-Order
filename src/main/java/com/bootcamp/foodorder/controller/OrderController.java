package com.bootcamp.foodorder.controller;

import com.bootcamp.foodorder.dto.CheckoutResponse;
import com.bootcamp.foodorder.dto.OrderResponse;
import com.bootcamp.foodorder.service.OrderService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/orders")
@Tag(name = "Order", description = "Order checkout and order history endpoints")
@SecurityRequirement(name = "bearerAuth")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping("/checkout")
    public ResponseEntity<CheckoutResponse> checkout(Authentication authentication) {
        CheckoutResponse response = orderService.checkout(authentication);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<List<OrderResponse>> getOrderHistory(Authentication authentication) {
        List<OrderResponse> response = orderService.getOrderHistory(authentication);
        return ResponseEntity.ok(response);
    }
}
