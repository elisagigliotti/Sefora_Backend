package it.unical.inf.ea.sefora_backend.controller;

import it.unical.inf.ea.sefora_backend.dto.WishlistDto;
import it.unical.inf.ea.sefora_backend.service.WishlistService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/wishlist")
public class WishlistController {

    @Autowired
    private WishlistService wishlistService;

    @PostMapping
    public ResponseEntity<WishlistDto> createWishlist(@RequestBody @Valid WishlistDto wishlistDto) {
        return ResponseEntity.ok(wishlistService.createWishlist(wishlistDto));
    }

    @PutMapping
    public ResponseEntity<String> updateWishlist(@RequestBody @Valid WishlistDto wishlistDto) {
        wishlistService.updateWishlist(wishlistDto);
        return ResponseEntity.ok("Wishlist updated!");
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteWishlist(@PathVariable("id") Long id) {
        wishlistService.deleteWishlist(id);
        return ResponseEntity.ok("Wishlist deleted!");
    }

    @GetMapping("/{id}")
    public ResponseEntity<WishlistDto> getWishlistById(@PathVariable("id") Long id) {
        return ResponseEntity.ok(wishlistService.getWishlistById(id));
    }

    @GetMapping("/user/{id}")
    public ResponseEntity<List<WishlistDto>> getWishlistsByOwner(@PathVariable("id") Long id) {
        return ResponseEntity.ok(wishlistService.getWishlistsByOwner(id));
    }
}
