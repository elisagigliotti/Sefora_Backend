package it.unical.inf.ea.sefora_backend.dao;

import it.unical.inf.ea.sefora_backend.entities.Product;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

@Transactional
public interface ProductDao extends JpaRepository<Product, Integer> {
    Optional<Product> findById(Long id);

    Product save(Product prodotto);

    void deleteById(Long id);

    List<Product> findAllByProductAccount_Id(Long accountId);
}
