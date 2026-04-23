package com.bootcamp.foodorder.service;

import com.bootcamp.foodorder.dto.AddToCartRequest;
import com.bootcamp.foodorder.dto.CartItemResponse;
import com.bootcamp.foodorder.dto.CartResponse;
import com.bootcamp.foodorder.dto.UpdateCartRequest;
import com.bootcamp.foodorder.entity.CartItem;
import com.bootcamp.foodorder.entity.Menu;
import com.bootcamp.foodorder.entity.User;
import com.bootcamp.foodorder.repository.CartItemRepository;
import com.bootcamp.foodorder.repository.MenuRepository;
import com.bootcamp.foodorder.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.util.List;

@Service
public class CartService {

    private final CartItemRepository cartItemRepository;
    private final MenuRepository menuRepository;
    private final UserRepository userRepository;

    public CartService(CartItemRepository cartItemRepository,
                       MenuRepository menuRepository,
                       UserRepository userRepository) {
        this.cartItemRepository = cartItemRepository;
        this.menuRepository = menuRepository;
        this.userRepository = userRepository;
    }

    public CartResponse getCart(Authentication authentication) {
        User user = getCurrentUser(authentication);
        List<CartItem> cartItems = cartItemRepository.findByUser(user);

        List<CartItemResponse> itemResponses = cartItems.stream()
                .map(this::mapToCartItemResponse)
                .toList();

        BigDecimal totalAmount = itemResponses.stream()
                .map(CartItemResponse::getSubtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return CartResponse.builder()
                .items(itemResponses)
                .totalAmount(totalAmount)
                .build();
    }

    public CartResponse addToCart(AddToCartRequest request, Authentication authentication) {
        User user = getCurrentUser(authentication);

        Menu menu = menuRepository.findById(request.getMenuId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Menu not found"));

        if (menu.getStock() <= 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Menu is out of stock");
        }

        CartItem cartItem = cartItemRepository.findByUserAndMenu(user, menu)
                .orElse(null);

        if (cartItem == null) {
            if (request.getQuantity() > menu.getStock()) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Quantity exceeds available stock");
            }

            cartItem = CartItem.builder()
                    .user(user)
                    .menu(menu)
                    .quantity(request.getQuantity())
                    .build();
        } else {
            int newQuantity = cartItem.getQuantity() + request.getQuantity();

            if (newQuantity > menu.getStock()) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Quantity exceeds available stock");
            }

            cartItem.setQuantity(newQuantity);
        }

        cartItemRepository.save(cartItem);
        return getCart(authentication);
    }

    public CartResponse updateCartItem(Long cartItemId, UpdateCartRequest request, Authentication authentication) {
        User user = getCurrentUser(authentication);

        CartItem cartItem = cartItemRepository.findByIdAndUser(cartItemId, user)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Cart item not found"));

        Menu menu = cartItem.getMenu();

        if (request.getQuantity() > menu.getStock()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Quantity exceeds available stock");
        }

        cartItem.setQuantity(request.getQuantity());
        cartItemRepository.save(cartItem);

        return getCart(authentication);
    }

    public String deleteCartItem(Long cartItemId, Authentication authentication) {
        User user = getCurrentUser(authentication);

        CartItem cartItem = cartItemRepository.findByIdAndUser(cartItemId, user)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Cart item not found"));

        cartItemRepository.delete(cartItem);
        return "Cart item deleted successfully";
    }

    private User getCurrentUser(Authentication authentication) {
        String email = authentication.getName();

        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
    }

    private CartItemResponse mapToCartItemResponse(CartItem cartItem) {
        BigDecimal price = cartItem.getMenu().getPrice();
        BigDecimal subtotal = price.multiply(BigDecimal.valueOf(cartItem.getQuantity()));

        return CartItemResponse.builder()
                .cartItemId(cartItem.getId())
                .menuId(cartItem.getMenu().getId())
                .menuName(cartItem.getMenu().getName())
                .price(price)
                .quantity(cartItem.getQuantity())
                .subtotal(subtotal)
                .build();
    }
}
