package com.bob.dreamshops.service.order;

import com.bob.dreamshops.dto.OrderDto;

import java.util.List;

public interface IOrderService {
    OrderDto placeOrder(Long userId);

    OrderDto getOrder(Long orderId);

    List<OrderDto> getOrdersByUserId(Long userId);
}
