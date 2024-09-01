package it.unical.inf.ea.sefora_backend.service;

import it.unical.inf.ea.sefora_backend.dto.WishlistDto;

import java.security.Principal;
import java.util.List;

public interface WishlistService {
    WishlistDto createWishlist(WishlistDto wishlistDto, Principal currentUser);

    void updateWishlist(WishlistDto wishlistDto, Principal currentUser);

    void deleteWishlist(Long id, Principal currentUser);

    WishlistDto getWishlistById(Long id);

    List<WishlistDto> getCurrentUserWishlist(Principal currentUser);

    List<WishlistDto> getSharedWishlists(Principal currentUser);

    void addUserToWishlist(Long userId, Principal currentUser);

    void addUserThroughEmailToWishlist(String userEmail, Principal currentUser);

    void removeUserFromWishlist(Long userId, Principal currentUser);

    void addProductToWishlist(Long productId, Principal currentUser);

    void removeProductFromWishlist(Long productId, Principal currentUser);

    List<WishlistDto> getAllAccessibleWishlists(Principal currentUser);
}
