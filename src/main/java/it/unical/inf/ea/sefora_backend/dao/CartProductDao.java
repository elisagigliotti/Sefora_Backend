package it.unical.inf.ea.sefora_backend.dao;

import it.unical.inf.ea.sefora_backend.entities.CartProduct;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CartProductDao extends JpaRepository<CartProduct, Long> {
    Optional<CartProduct> findById(Long id);

    List<CartProduct> findByCart_Id(Long cartId);

    void deleteById(Long id);
}
