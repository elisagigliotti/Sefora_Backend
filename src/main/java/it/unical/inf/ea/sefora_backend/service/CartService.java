package it.unical.inf.ea.sefora_backend.service;

import it.unical.inf.ea.sefora_backend.dto.CartDto;
import org.springframework.stereotype.Service;

import java.security.Principal;

@Service
public interface CartService {
    CartDto createCart(CartDto cartDto, Principal currentUser);

    void updateCart(CartDto cartDto, Principal currentUser);

    CartDto getCartDtoById(Long id);

    CartDto getCurrentUserCart(Principal currentUser);

    void addProductToCart(Long cartId, Long productId, Principal currentUser);

    void removeProductFromCart(Long cartId, Long productId, Principal currentUser);

    void checkoutCart(Long cartId, Principal currentUser);
}
