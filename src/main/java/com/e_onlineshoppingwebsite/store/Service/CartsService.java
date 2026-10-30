package com.e_onlineshoppingwebsite.store.Service;

import java.math.BigDecimal;
import java.util.List;

import com.e_onlineshoppingwebsite.store.Model.Cart;
import com.e_onlineshoppingwebsite.store.Model.CartItem;

public interface CartsService {

    Cart getCartByUserId(Long userId);

    Cart createCart(Long userId);

    List<CartItem> getCartItems(Long userId);

    void addToCart(Long userId, Long productId, int quantity);

    void updateQuantity(
            Long userId, Long productId, int quantity);

    void removeFromCart(Long userId, Long productId);

    void clearCart(Long userId);

    BigDecimal calculateTotal(Long userId);

    int countCartItems(Long userId);

    boolean isProductInCart(Long userId, Long productId);
}