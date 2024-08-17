package it.unical.inf.ea.sefora_backend.service;

import it.unical.inf.ea.sefora_backend.dto.CartDto;
import org.springframework.stereotype.Service;

@Service
public interface CartService {
    CartDto createCart(CartDto cartDto);

    CartDto getCartByUserId(Long id);

    void updateCart(CartDto cartDto);

    CartDto getCartById(Long id);
}
