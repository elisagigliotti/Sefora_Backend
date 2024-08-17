package it.unical.inf.ea.sefora_backend.controller;

import it.unical.inf.ea.sefora_backend.dto.ProductDto;
import it.unical.inf.ea.sefora_backend.service.ProductService;
import jakarta.validation.Valid;
import org.apache.coyote.BadRequestException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/product")
public class ProductController {

    @Autowired
    private ProductService productService;

    @PostMapping
    public ResponseEntity<ProductDto> createProduct(@RequestBody @Valid ProductDto request) {
        return ResponseEntity.ok(productService.save(request));
    }

    @GetMapping("/product/{id}")
    public ResponseEntity<ProductDto> getProductById(@PathVariable("id") Long id) {
        return ResponseEntity.ok(productService.getProductById(id));
    }

    @GetMapping("/all")
    public ResponseEntity<List<ProductDto>> getAllProducts() {
        return ResponseEntity.ok(productService.getAllProducts());
    }

    @GetMapping("/user/{id}")
    public ResponseEntity<List<ProductDto>> getAllProductsByOwner(@PathVariable("id") Long id) {
        return ResponseEntity.ok(productService.getAllProductsByOwner(id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteProduct(@PathVariable("id") Long id) throws BadRequestException {
        productService.deleteProduct(id);
        return ResponseEntity.ok("Product deleted!");
    }
}
