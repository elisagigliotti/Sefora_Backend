package it.unical.inf.ea.sefora_backend.controller;

import it.unical.inf.ea.sefora_backend.dto.CartDto;
import it.unical.inf.ea.sefora_backend.service.CartService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@RequestMapping("/api/cart")
public class CartController {

    @Autowired
    private CartService cartService;

    @PostMapping
    public ResponseEntity<CartDto> createCart(@RequestBody @Valid CartDto cartDto, Principal currentUser) {
        return ResponseEntity.ok(cartService.createCart(cartDto, currentUser));
    }

    @PutMapping
    public ResponseEntity<String> updateCart(@RequestBody @Valid CartDto cartDto, Principal currentUser) {
        cartService.updateCart(cartDto, currentUser);
        return ResponseEntity.ok("Cart updated!");
    }

    @GetMapping("/{id}")
    public ResponseEntity<CartDto> getCartById(@PathVariable("id") Long id) {
        return ResponseEntity.ok(cartService.getCartDtoById(id));
    }

    @GetMapping("/current")
    public ResponseEntity<CartDto> getCurrentUserCart(Principal connectedUser) {
        return ResponseEntity.ok(cartService.getCurrentUserCart(connectedUser));
    }

    @PatchMapping("/addProduct")
    public ResponseEntity<String> addProductToCart(@RequestParam("cartId") Long cartId, @RequestParam("productId") Long productId, Principal currentUser) {
        cartService.addProductToCart(cartId, productId, currentUser);
        return ResponseEntity.ok("Product added to cart!");
    }

    @PatchMapping("/removeProduct")
    public ResponseEntity<String> removeProductFromCart(@RequestParam("cartId") Long cartId, @RequestParam("productId") Long productId, Principal currentUser) {
        cartService.removeProductFromCart(cartId, productId, currentUser);
        return ResponseEntity.ok("Product removed from cart!");
    }

    @PatchMapping("/checkout")
    public ResponseEntity<String> checkoutCart(@RequestParam("cartId") Long cartId, Principal currentUser) {
        cartService.checkoutCart(cartId, currentUser);
        return ResponseEntity.ok("Cart checked out!");
    }
}
