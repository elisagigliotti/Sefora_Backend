package it.unical.inf.ea.sefora_backend.service;

import it.unical.inf.ea.sefora_backend.dto.WishlistProductDto;

import java.util.List;


public interface WishlistProductService {

    void deleteWishlistProduct(Long id);

    WishlistProductDto createWishlistProduct(WishlistProductDto wishlistProductDto);

    List<WishlistProductDto> findAllWishlistProduct(Long id);
}
