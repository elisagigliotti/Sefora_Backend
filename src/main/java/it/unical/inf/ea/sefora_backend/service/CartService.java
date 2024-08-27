package it.unical.inf.ea.sefora_backend.service;

import it.unical.inf.ea.sefora_backend.dto.CartDto;
import it.unical.inf.ea.sefora_backend.dto.OrderDto;
import org.springframework.stereotype.Service;

import java.security.Principal;

@Service
public interface CartService {
    CartDto createCart(CartDto cartDto);

    CartDto getCartByUserId(Long id);

    void updateCart(CartDto cartDto);

    CartDto getCartById(Long id);

    OrderDto createOrder(OrderDto orderDto);

    CartDto getCurrentUserCart(Principal currentUser);
}
