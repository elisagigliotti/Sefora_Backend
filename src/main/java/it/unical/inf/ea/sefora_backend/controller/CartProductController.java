package it.unical.inf.ea.sefora_backend.controller;

import it.unical.inf.ea.sefora_backend.dto.CartProductDto;
import it.unical.inf.ea.sefora_backend.service.CartProductService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/cartproduct")
public class CartProductController {

    @Autowired
    private CartProductService cartProductService;

    @PostMapping
    public ResponseEntity<CartProductDto> createCartProduct(@RequestBody @Valid CartProductDto cartProductDto) {
        return ResponseEntity.ok(cartProductService.createCartProduct(cartProductDto));
    }

    @PutMapping
    public ResponseEntity<String> updateCartProduct(@RequestBody @Valid CartProductDto request) {
        cartProductService.updateCartProduct(request);
        return ResponseEntity.ok("CartProduct updated!");
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteCartProduct(@PathVariable("id") Long id) {
        cartProductService.deleteCartProduct(id);
        return ResponseEntity.ok("CartProduct deleted!");
    }

    @GetMapping("/cart/{id}")
    public ResponseEntity<List<CartProductDto>> getAllCartProductByCartId(@PathVariable("id") Long id) {
        return ResponseEntity.ok(cartProductService.getAllCartProductByCartId(id));
    }
}
