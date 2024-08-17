package it.unical.inf.ea.sefora_backend.service;

import it.unical.inf.ea.sefora_backend.dao.OrderDao;
import it.unical.inf.ea.sefora_backend.dao.OrderProductDao;
import it.unical.inf.ea.sefora_backend.dao.ProductDao;
import it.unical.inf.ea.sefora_backend.dao.UserDao;
import it.unical.inf.ea.sefora_backend.dto.OrderProductDto;
import it.unical.inf.ea.sefora_backend.entities.Order;
import it.unical.inf.ea.sefora_backend.entities.OrderProduct;
import it.unical.inf.ea.sefora_backend.entities.Product;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class OrderProductServiceImpl implements OrderProductService {

    @Autowired
    private ProductDao productDao;

    @Autowired
    private OrderProductDao orderProductDao;

    @Autowired
    private UserDao userDao;

    @Autowired
    private OrderDao orderDao;

    private OrderProductDto convertToDto(OrderProduct orderProduct) {
        OrderProductDto orderProductDto = new OrderProductDto();
        orderProductDto.setOrderId(orderProduct.getOrder().getId());
        orderProductDto.setProductId(orderProduct.getProduct().getId());
        orderProductDto.setQuantity(orderProduct.getQuantity());
        orderProductDto.setId(orderProduct.getId());
        return orderProductDto;
    }

    private OrderProduct convertToEntity(OrderProductDto orderProductDto) {
        OrderProduct orderProduct = new OrderProduct();
        Order order = orderDao.findById(orderProductDto.getOrderId()).orElseThrow(() -> new RuntimeException("Order not found!"));
        Product product = productDao.findById(orderProductDto.getProductId()).orElseThrow(() -> new RuntimeException("Product not found!"));
        orderProduct.setOrder(order);
        orderProduct.setProduct(product);
        orderProduct.setQuantity(orderProductDto.getQuantity());
        orderProduct.setId(orderProductDto.getId());
        return orderProduct;
    }

    @Override
    public OrderProductDto saveOrderProduct(OrderProductDto orderProductDto) {
        OrderProduct orderProduct = convertToEntity(orderProductDto);
        return convertToDto(orderProductDao.save(orderProduct));
    }

    @Override
    public List<OrderProductDto> getOrderProductsByOrderId(Long orderId) {
        if (orderDao.findById(orderId).isEmpty())
            throw new RuntimeException("Order not found!");

        return orderProductDao.findAllByOrder_Id(orderId)
                .stream().map(this::convertToDto).collect(Collectors.toList());
    }
}