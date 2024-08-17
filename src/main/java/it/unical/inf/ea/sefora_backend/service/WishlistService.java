package it.unical.inf.ea.sefora_backend.service;

import it.unical.inf.ea.sefora_backend.dto.WishlistDto;

import java.util.List;

public interface WishlistService {
    WishlistDto createWishlist(WishlistDto wishlistDto);

    void updateWishlist(WishlistDto wishlistDto);

    void deleteWishlist(Long id);

    WishlistDto getWishlistById(Long id);

    List<WishlistDto> getWishlistsByOwner(Long id);
}
