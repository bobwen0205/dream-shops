package com.bob.dreamshops.service.cart;

import com.bob.dreamshops.model.Cart;
import com.bob.dreamshops.model.User;

import java.math.BigDecimal;

public interface ICartService {
    Cart getCart(Long id);

    void clearCart(Long id);

    BigDecimal getTotalPrice(Long id);

    Cart getCartByUserId(Long userId);

    Cart initializeNewCart(User user);
}
