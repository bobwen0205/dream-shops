package com.bob.dreamshops.service.cart;

import com.bob.dreamshops.exceptions.ResourceNotFoundException;
import com.bob.dreamshops.model.Cart;
import com.bob.dreamshops.model.CartItem;
import com.bob.dreamshops.model.Product;
import com.bob.dreamshops.service.product.IProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CartItemService implements ICartItemService {
    private final IProductService productService;
    private final ICartService cartService;

    @Override
    @Transactional
    public void addItemToCart(Long cartId, Long productId, int quantity) {
        // 1. Get the cart
        // 2. Get the product
        // 3. Check if the product already in the cart
        // 4. If yes, then increase the quantity with the requested quantity
        // 5. If no, then initiate a new CartItem entry
        Cart cart = cartService.getCart(cartId);
        Product product = productService.getProductById(productId);
        CartItem cartItem = cart.getItems().stream()
                .filter(item -> item.getProduct().getId().equals(product.getId()))
                .findFirst().orElse(new CartItem());
        if (cartItem.getId() == null) {
            cartItem.setCart(cart);
            cartItem.setProduct(product);
            cartItem.setQuantity(quantity);
            cartItem.setUnitPrice(product.getPrice());
            cart.addItem(cartItem);
        } else {
            cartItem.setQuantity(cartItem.getQuantity() + quantity);
        }
        cart.updateTotalAmount();
        cartItem.setTotalPrice();
    }

    @Override
    @Transactional
    public void removeItemFromCart(Long cartId, Long productId) {
        Cart cart = cartService.getCart(cartId);
        CartItem itemToRemove = getCartItem(cartId, productId);
        cart.removeItem(itemToRemove);
    }

    @Override
    @Transactional
    public void updateItemQuantity(Long cartId, Long productId, int quantity) {
        Cart cart = cartService.getCart(cartId);
        cart.getItems().stream()
                .filter(item -> item.getProduct().getId().equals(productId))
                .findFirst()
                .ifPresent(item -> {
                    item.setQuantity(quantity);
                    item.setTotalPrice();
                });
        cart.updateTotalAmount();
    }

    @Override
    public CartItem getCartItem(Long cartId, Long productId) {
        return cartService.getCart(cartId).getItems().stream()
                .filter(item -> item.getProduct().getId().equals(productId))
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("CartItem Not Found"));
    }
}
