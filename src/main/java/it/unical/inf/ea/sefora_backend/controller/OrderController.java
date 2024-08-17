package it.unical.inf.ea.sefora_backend.controller;

import it.unical.inf.ea.sefora_backend.dto.OrderDto;
import it.unical.inf.ea.sefora_backend.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/order")
public class OrderController {

    @Autowired
    private OrderService orderService;

    @PostMapping
    public ResponseEntity<OrderDto> createOrder(@RequestBody OrderDto order) {
        return ResponseEntity.ok(orderService.createOrder(order));
    }

    @GetMapping("/user/{id}")
    public ResponseEntity<List<OrderDto>> findOrdersbyUserId(@PathVariable("id") Long id) {
        List<OrderDto> orderDtos = orderService.findOrdersByUserId(id);
        return (orderDtos != null && !orderDtos.isEmpty()) ? ResponseEntity.ok(orderDtos) : ResponseEntity.notFound().build();
    }
}
