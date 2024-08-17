package it.unical.inf.ea.sefora_backend.controller;

import it.unical.inf.ea.sefora_backend.dto.WishlistProductDto;
import it.unical.inf.ea.sefora_backend.service.WishlistProductService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/wishlist-prodotti")
public class WishlistProductController {

    @Autowired
    private WishlistProductService wishlistProductService;

    @PostMapping
    public ResponseEntity<WishlistProductDto> createWishlistProduct(@RequestBody @Valid WishlistProductDto wishlistProductDto) {
        return ResponseEntity.ok(wishlistProductService.createWishlistProduct(wishlistProductDto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteWishlistProduct(@PathVariable("id") Long id) {
        wishlistProductService.deleteWishlistProduct(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/wishlist/{id}")
    public ResponseEntity<List<WishlistProductDto>> getAllWishlistProductByWishlistId(@PathVariable("id") Long id) {
        return ResponseEntity.ok(wishlistProductService.findAllWishlistProduct(id));
    }
}