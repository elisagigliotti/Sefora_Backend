package it.unical.inf.ea.sefora_backend.controller;

import it.unical.inf.ea.sefora_backend.dto.PurchaseDto;
import it.unical.inf.ea.sefora_backend.service.PurchaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/order")
public class PurchaseController {

    @Autowired
    private PurchaseService purchaseService;

    @PostMapping
    public ResponseEntity<PurchaseDto> createOrder(@RequestBody PurchaseDto order, Principal currentUser) {
        return ResponseEntity.ok(purchaseService.createOrder(order, currentUser));
    }

    @GetMapping("/user/{id}")
    public ResponseEntity<List<PurchaseDto>> findOrdersByUserId(@PathVariable("id") Long id) {
        List<PurchaseDto> purchaseDtos = purchaseService.findOrdersByUserId(id);
        return (purchaseDtos != null && !purchaseDtos.isEmpty()) ? ResponseEntity.ok(purchaseDtos) : ResponseEntity.notFound().build();
    }

    @GetMapping("/current")
    public ResponseEntity<List<PurchaseDto>> findOrdersByCurrentUser(Principal currentUser) {
        return ResponseEntity.ok(purchaseService.findOrdersByCurrentUser(currentUser));
    }

    @PatchMapping("/convert/{id}")
    public ResponseEntity<PurchaseDto> convertProductToPurchase(@PathVariable("id") Long id, Principal currentUser) {
        return ResponseEntity.ok(purchaseService.convertProductToPurchase(id, currentUser));
    }
}
