package it.unical.inf.ea.sefora_backend.service;

import it.unical.inf.ea.sefora_backend.dto.CartProductDto;

import java.util.List;

public interface CartProductService {
    CartProductDto createCartProduct(CartProductDto cartProductDto);

    void updateCartProduct(CartProductDto cartProductDto);

    void deleteCartProduct(Long id);

    List<CartProductDto> getAllCartProductByCartId(Long id);
}
