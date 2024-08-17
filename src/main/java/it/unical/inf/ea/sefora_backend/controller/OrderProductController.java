package it.unical.inf.ea.sefora_backend.controller;

import it.unical.inf.ea.sefora_backend.dto.OrderProductDto;
import it.unical.inf.ea.sefora_backend.service.OrderProductService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/orderProduct")
public class OrderProductController {

    @Autowired
    private OrderProductService orderProductService;

    @PostMapping
    public ResponseEntity<OrderProductDto> createOrderProduct(@RequestBody @Valid OrderProductDto request) {
        return ResponseEntity.ok(orderProductService.saveOrderProduct(request));
    }

    @GetMapping("/order/{id}")
    public ResponseEntity<List<OrderProductDto>> getOrderProductsByOrderId(@PathVariable("id") Long id) {
        return ResponseEntity.ok(orderProductService.getOrderProductsByOrderId(id));
    }
}
