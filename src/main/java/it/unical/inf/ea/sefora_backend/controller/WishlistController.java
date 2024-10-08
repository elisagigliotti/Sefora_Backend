package it.unical.inf.ea.sefora_backend.controller;

import it.unical.inf.ea.sefora_backend.dto.ProductShortDto;
import it.unical.inf.ea.sefora_backend.dto.WishlistDto;
import it.unical.inf.ea.sefora_backend.service.WishlistService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/wishlist")
public class WishlistController {

    @Autowired
    private WishlistService wishlistService;

    @PostMapping
    public ResponseEntity<WishlistDto> createWishlist(@RequestBody @Valid WishlistDto wishlistDto, Principal currentUser) {
        return ResponseEntity.ok(wishlistService.createWishlist(wishlistDto, currentUser));
    }

    @PutMapping
    public ResponseEntity<String> updateWishlist(@RequestBody @Valid WishlistDto wishlistDto, Principal currentUser) {
        wishlistService.updateWishlist(wishlistDto, currentUser);
        return ResponseEntity.ok("Wishlist updated!");
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteWishlist(@PathVariable("id") Long id, Principal currentUser) {
        wishlistService.deleteWishlist(id, currentUser);
        return ResponseEntity.ok("Wishlist deleted!");
    }

    @GetMapping("/{id}")
    public ResponseEntity<WishlistDto> getWishlistById(@PathVariable("id") Long id) {
        return ResponseEntity.ok(wishlistService.getWishlistById(id));
    }

    @GetMapping("/current")
    public ResponseEntity<List<WishlistDto>> getCurrentUserWishlists(Principal currentUser) {
        return ResponseEntity.ok(wishlistService.getCurrentUserWishlist(currentUser));
    }

    @GetMapping("/shared")
    public ResponseEntity<List<WishlistDto>> getSharedWishlists(Principal currentUser) {
        return ResponseEntity.ok(wishlistService.getSharedWishlists(currentUser));
    }

    @PatchMapping("/addUser")
    public ResponseEntity<String> addUserToWishlist(@RequestParam("userId") Long userId, Principal currentUser) {
        wishlistService.addUserToWishlist(userId, currentUser);
        return ResponseEntity.ok("User added to wishlist!");
    }

    @PatchMapping("/addUserByEmail")
    public ResponseEntity<String> addUserThroughEmailToWishlist(@RequestParam("userEmail") String userEmail, Principal currentUser) {
        wishlistService.addUserThroughEmailToWishlist(userEmail, currentUser);
        return ResponseEntity.ok("User added to wishlist!");
    }

    @PatchMapping("/removeUser")
    public ResponseEntity<String> removeUserFromWishlist(@RequestParam("userId") Long userId, Principal currentUser) {
        wishlistService.removeUserFromWishlist(userId, currentUser);
        return ResponseEntity.ok("User removed from wishlist!");
    }

    @PatchMapping("/addProduct")
    public ResponseEntity<String> addProductToWishlist(@RequestParam("productId") Long productId, Principal currentUser) {
        wishlistService.addProductToWishlist(productId, currentUser);
        return ResponseEntity.ok("Product added to wishlist!");
    }

    @PatchMapping("/removeProduct")
    public ResponseEntity<String> removeProductFromWishlist(@RequestParam("productId") Long productId, Principal currentUser) {
        wishlistService.removeProductFromWishlist(productId, currentUser);
        return ResponseEntity.ok("Product removed from wishlist!");
    }

    @GetMapping("/accessible")
    public ResponseEntity<List<WishlistDto>> getAllAccessibleWishlists(Principal currentUser) {
        return ResponseEntity.ok(wishlistService.getAllAccessibleWishlists(currentUser));
    }
}
