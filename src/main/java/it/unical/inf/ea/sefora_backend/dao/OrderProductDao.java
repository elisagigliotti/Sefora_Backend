package it.unical.inf.ea.sefora_backend.dao;

import it.unical.inf.ea.sefora_backend.entities.OrderProduct;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface OrderProductDao extends JpaRepository<OrderProduct, Long> {
    Optional<OrderProduct> findById(Long id);

    List<OrderProduct> findAll();

    List<OrderProduct> findAllByOrder_Id(Long orderId);

    OrderProduct save(OrderProduct orderProduct);
}