package it.unical.inf.ea.sefora_backend.dao;

import it.unical.inf.ea.sefora_backend.entities.Cart;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface CartDao extends JpaRepository<Cart, Long> {

    @Query("SELECT c FROM Cart c LEFT JOIN FETCH c.cartProducts WHERE c.id = :id")
    Optional<Cart> findById(@Param("id") Long id);

    Cart save(Cart cart);

    void deleteById(Long id);

    @Query("SELECT c FROM Cart c LEFT JOIN FETCH c.cartProducts WHERE c.id = :id")
    Optional<Cart> findByIdWithProducts(@Param("id") Long id);

    @Query("SELECT c FROM Cart c LEFT JOIN FETCH c.cartProducts WHERE c.cartAccount.id = :accountId")
    Optional<Cart> findByUserIdWithProducts(@Param("accountId") Long accountId);
}
