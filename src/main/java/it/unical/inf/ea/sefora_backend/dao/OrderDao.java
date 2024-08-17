package it.unical.inf.ea.sefora_backend.dao;

import it.unical.inf.ea.sefora_backend.entities.Order;
import it.unical.inf.ea.sefora_backend.entities.OrderProduct;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrderDao extends JpaRepository<Order, Long> {
    List<Order> findAllByUserOrder_Id(Long userId);

    OrderProduct getOrderProductsById(Long id);
}
