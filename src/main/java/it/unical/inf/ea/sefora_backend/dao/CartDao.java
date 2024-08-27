package it.unical.inf.ea.sefora_backend.dao;

import it.unical.inf.ea.sefora_backend.dto.OrderDto;
import it.unical.inf.ea.sefora_backend.entities.Cart;
import org.springframework.beans.PropertyValues;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CartDao extends JpaRepository<Cart, Long> {

    Optional<Cart> findById(Long id);

    Cart save(Cart cart);

    void deleteById(Long id);

    List<Cart> findByUserCart_Id(Long id);
}
