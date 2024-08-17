package it.unical.inf.ea.sefora_backend.service;

import it.unical.inf.ea.sefora_backend.dto.OrderProductDto;

import java.util.List;

public interface OrderProductService {
    OrderProductDto saveOrderProduct(OrderProductDto orderProductDto);

    List<OrderProductDto> getOrderProductsByOrderId(Long orderId);
}
