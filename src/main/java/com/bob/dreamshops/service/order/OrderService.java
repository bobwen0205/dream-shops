package com.bob.dreamshops.service.order;

import com.bob.dreamshops.dto.OrderDto;
import com.bob.dreamshops.enums.OrderStatus;
import com.bob.dreamshops.exceptions.ResourceNotFoundException;
import com.bob.dreamshops.model.Cart;
import com.bob.dreamshops.model.Order;
import com.bob.dreamshops.model.OrderItem;
import com.bob.dreamshops.model.Product;
import com.bob.dreamshops.repository.OrderRepository;
import com.bob.dreamshops.repository.ProductRepository;
import com.bob.dreamshops.service.cart.CartService;
import com.bob.dreamshops.service.user.UserService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderService implements IOrderService {
    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;
    private final CartService cartService;
    private final ModelMapper modelMapper;
    private final UserService userService;

    @Override
    public OrderDto placeOrder(Long userId) {
        Cart cart = cartService.getCartByUserId(userId);
        Order order = createOrder(cart);
        List<OrderItem> orderItems = createOrderItems(order, cart);
        order.setItems(new HashSet<>(orderItems));
        order.setTotalAmount(calculateTotalAmount(order));
        userService.getUserById(userId).setCart(null);
        // cartService.clearCart(cart.getId());
        return convertToDto(orderRepository.save(order));
    }

    @Override
    public OrderDto getOrder(Long orderId) {
        return orderRepository.findById(orderId)
                .map(this::convertToDto)
                .orElseThrow(() -> new ResourceNotFoundException("Order Not Found"));
    }

    @Override
    public List<OrderDto> getOrdersByUserId(Long userId) {
        return orderRepository.findByUserId(userId)
                .stream()
                .map(this::convertToDto)
                .toList();
    }

    private BigDecimal calculateTotalAmount(Order order) {
        return order.getItems().stream()
                .map(item -> item.getPrice()
                        .multiply(new BigDecimal(item.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private List<OrderItem> createOrderItems(Order order, Cart cart) {
        return cart.getItems().stream()
                .map(cartItem -> {
                    Product product = cartItem.getProduct();
                    product.setInventory(product.getInventory() - cartItem.getQuantity());
                    productRepository.save(product);
                    return new OrderItem(
                            order,
                            product,
                            cartItem.getQuantity(),
                            cartItem.getUnitPrice()
                    );
                }).toList();
    }

    private Order createOrder(Cart cart) {
        Order order = new Order();
        order.setUser(cart.getUser());
        order.setStatus(OrderStatus.PENDING);
        order.setDate(LocalDate.now());
        return order;
    }

    private OrderDto convertToDto(Order order) {
        return modelMapper.map(order, OrderDto.class);
    }
}
