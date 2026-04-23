package com.bootcamp.foodorder.controller;

import com.bootcamp.foodorder.dto.AddToCartRequest;
import com.bootcamp.foodorder.dto.CartResponse;
import com.bootcamp.foodorder.dto.UpdateCartRequest;
import com.bootcamp.foodorder.service.CartService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/cart")
@Tag(name = "Cart", description = "Cart management endpoints for customer")
@SecurityRequirement(name = "bearerAuth")
public class CartController {

    private final CartService cartService;

    public CartController(CartService cartService) {
        this.cartService = cartService;
    }

    @GetMapping
    public ResponseEntity<CartResponse> getCart(Authentication authentication) {
        CartResponse response = cartService.getCart(authentication);
        return ResponseEntity.ok(response);
    }

    @PostMapping
    public ResponseEntity<CartResponse> addToCart(
            @Valid @RequestBody AddToCartRequest request,
            Authentication authentication
    ) {
        CartResponse response = cartService.addToCart(request, authentication);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<CartResponse> updateCartItem(
            @PathVariable Long id,
            @Valid @RequestBody UpdateCartRequest request,
            Authentication authentication
    ) {
        CartResponse response = cartService.updateCartItem(id, request, authentication);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteCartItem(
            @PathVariable Long id,
            Authentication authentication
    ) {
        String response = cartService.deleteCartItem(id, authentication);
        return ResponseEntity.ok(response);
    }
}
