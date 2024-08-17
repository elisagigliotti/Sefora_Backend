package it.unical.inf.ea.sefora_backend.controller;

import it.unical.inf.ea.sefora_backend.dto.CartDto;
import it.unical.inf.ea.sefora_backend.service.CartService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/cart")
public class CartController {

    @Autowired
    private CartService cartService;

    @PostMapping
    public ResponseEntity<CartDto> createCart(@RequestBody @Valid CartDto cartDto) {
        return ResponseEntity.ok(cartService.createCart(cartDto));
    }

    @PutMapping
    public ResponseEntity<String> updateCart(@RequestBody @Valid CartDto cartDto) {
        cartService.updateCart(cartDto);
        return ResponseEntity.ok("Cart updated!");
    }

    @GetMapping("/{id}")
    public ResponseEntity<CartDto> getCartById(@PathVariable("id") Long id) {
        return ResponseEntity.ok(cartService.getCartById(id));
    }
}
