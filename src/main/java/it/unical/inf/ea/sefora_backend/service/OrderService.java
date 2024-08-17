package it.unical.inf.ea.sefora_backend.service;

import it.unical.inf.ea.sefora_backend.dto.OrderDto;

import java.util.List;

public interface OrderService {
    List<OrderDto> findOrdersByUserId(Long userid);

    OrderDto createOrder(OrderDto order);
}
