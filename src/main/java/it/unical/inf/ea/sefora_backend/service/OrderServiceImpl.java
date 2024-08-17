package it.unical.inf.ea.sefora_backend.service;

import it.unical.inf.ea.sefora_backend.dao.OrderDao;
import it.unical.inf.ea.sefora_backend.dao.ProductDao;
import it.unical.inf.ea.sefora_backend.dao.UserDao;
import it.unical.inf.ea.sefora_backend.dto.OrderDto;
import it.unical.inf.ea.sefora_backend.dto.OrderProductDto;
import it.unical.inf.ea.sefora_backend.entities.Order;
import it.unical.inf.ea.sefora_backend.entities.OrderProduct;
import it.unical.inf.ea.sefora_backend.entities.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class OrderServiceImpl implements OrderService {

    @Autowired
    private OrderDao orderDao;

    @Autowired
    private UserDao userDao;

    @Autowired
    private ProductDao productDao;

    private OrderDto convertToDto(Order order) {
        OrderDto orderDto = new OrderDto();
        orderDto.setId(order.getId());
        orderDto.setUserOrderId(order.getUserOrder().getId());
        orderDto.setPurchaseDate(order.getPurchaseDate());
        orderDto.setTotalOrderPrice(order.getTotalOrderPrice());
        orderDto.setAddress(order.getAddress());
        return orderDto;
    }

    private Order convertToEntity(OrderDto orderDto) {
        Order order = new Order();
        User user = userDao.findById(orderDto.getUserOrderId()).orElseThrow(() -> new RuntimeException("User not found"));
        order.setUserOrder(user);
        order.setPurchaseDate(orderDto.getPurchaseDate() != null ? orderDto.getPurchaseDate() : LocalDate.now());
        order.setTotalOrderPrice(orderDto.getTotalOrderPrice());
        order.setAddress(orderDto.getAddress());

        for (OrderProductDto orderProductDto : orderDto.getOrderProductsDto()) {
            OrderProduct orderProduct = new OrderProduct();
            orderProduct.setOrder(order);
            orderProduct.setProduct(productDao.findById(orderProductDto.getProductId()).orElseThrow(() -> new RuntimeException("Product not found")));
            orderProduct.setQuantity(orderProductDto.getQuantity());
            order.getOrderProducts().add(orderProduct);
        }

        return order;
    }

    @Override
    public List<OrderDto> findOrdersByUserId(Long id) {
        return orderDao.findAllByUserOrder_Id(id).stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Override
    public OrderDto createOrder(OrderDto orderDto) {
        Order order = convertToEntity(orderDto);
        return convertToDto(orderDao.save(order));
    }
}