package it.unical.inf.ea.sefora_backend.service;

import it.unical.inf.ea.sefora_backend.dto.PurchaseDto;

import java.security.Principal;
import java.util.List;

public interface PurchaseService {
    List<PurchaseDto> findOrdersByUserId(Long userid);

    PurchaseDto createOrder(PurchaseDto order, Principal currentUser);

    PurchaseDto createOrderFromCartId(Long cartId, Principal currentUser);

    List<PurchaseDto> findOrdersByCurrentUser(Principal currentUser);

    PurchaseDto convertProductToPurchase(Long id, Principal currentUser);
}
