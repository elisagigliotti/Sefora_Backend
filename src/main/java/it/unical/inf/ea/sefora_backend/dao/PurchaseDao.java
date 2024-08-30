package it.unical.inf.ea.sefora_backend.dao;

import it.unical.inf.ea.sefora_backend.entities.Purchases;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrderDao extends JpaRepository<Purchases, Long> {
    List<Purchases> findAllByOrderAccount_Id(Long accountId);

    List<Long> getOrderProductsById(Long id);
}
