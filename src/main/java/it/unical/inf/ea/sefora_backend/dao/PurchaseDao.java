package it.unical.inf.ea.sefora_backend.dao;

import it.unical.inf.ea.sefora_backend.entities.Purchase;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

@Transactional
public interface PurchaseDao extends JpaRepository<Purchase, Long> {
    @Query("SELECT p FROM Purchase p LEFT JOIN FETCH p.purchaseProducts WHERE p.purchaseAccount.id = :accountId")
    List<Purchase> findAllByPurchaseAccount_Id(@Param("accountId") Long accountId);

    List<Long> getOrderProductsById(Long id);
}
