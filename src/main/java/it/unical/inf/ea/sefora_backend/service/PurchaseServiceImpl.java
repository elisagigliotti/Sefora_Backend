package it.unical.inf.ea.sefora_backend.service;

import it.unical.inf.ea.sefora_backend.dao.CartDao;
import it.unical.inf.ea.sefora_backend.dao.OrderDao;
import it.unical.inf.ea.sefora_backend.dao.ProductDao;
import it.unical.inf.ea.sefora_backend.dao.AccountDao;
import it.unical.inf.ea.sefora_backend.dto.OrderDto;
import it.unical.inf.ea.sefora_backend.dto.ProductShortDto;
import it.unical.inf.ea.sefora_backend.entities.Account;
import it.unical.inf.ea.sefora_backend.entities.Purchases;
import it.unical.inf.ea.sefora_backend.entities.Product;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class OrderServiceImpl implements OrderService {

    @Autowired
    private OrderDao orderDao;

    @Autowired
    private AccountDao accountDao;

    @Autowired
    private ProductDao productDao;

    @Autowired
    private CartDao cartDao;

    private OrderDto convertToDto(Purchases purchases) {
        OrderDto orderDto = new OrderDto();

        Long userId = accountDao.findById(purchases.getPurchaseAccount().getId()).orElseThrow(() -> new RuntimeException("User not found!")).getId();
        orderDto.setUserOrderId(userId);

        orderDto.setPurchaseDate(purchases.getPurchaseDate());
        orderDto.setTotalOrderPrice(purchases.getTotalOrderPrice());

        if(orderDto.getProducts() == null)
            orderDto.setProducts(new ArrayList<>());

        for (Product product : purchases.getPurchaseProducts()) {
            if (productDao.findById(product.getId()).isEmpty())
                throw new RuntimeException("Product not found!");

            ProductShortDto productShortDto = new ProductShortDto();
            productShortDto.setId(product.getId());
            productShortDto.setName(product.getName());
            productShortDto.setPrice(product.getPrice());
            orderDto.getProducts().add(productShortDto);
        }

        orderDto.setId(purchases.getId());
        return orderDto;
    }

    private Purchases convertToEntity(OrderDto orderDto) {
        Purchases purchases = new Purchases();
        purchases.setPurchaseProducts(new ArrayList<>());
        Account account = accountDao.findById(orderDto.getUserOrderId()).orElseThrow(() -> new RuntimeException("User not found!"));
        purchases.setPurchaseAccount(account);
        purchases.setPurchaseDate(orderDto.getPurchaseDate());
        purchases.setTotalOrderPrice(orderDto.getTotalOrderPrice());

        if(purchases.getPurchaseProducts() == null)
            purchases.setPurchaseProducts(new ArrayList<>());

        System.out.println(orderDto.getProducts());
        for (ProductShortDto productShortDto : orderDto.getProducts()) {
            Product product = productDao.findById(productShortDto.getId()).orElseThrow(() -> new RuntimeException("Product not found!"));
            purchases.getPurchaseProducts().add(product);
        }

        purchases.setId(orderDto.getId());
        return purchases;
    }

    @Override
    public List<OrderDto> findOrdersByUserId(Long id) {
        return orderDao.findAllByOrderAccount_Id(id).stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Override
    public OrderDto createOrder(OrderDto orderDto, Principal currentUser) {
        var user = (Account) ((UsernamePasswordAuthenticationToken) currentUser).getPrincipal();

        if (!Objects.equals(user.getId(), orderDto.getUserOrderId()))
            throw new RuntimeException("User not allowed to create order for another user!");

        Purchases purchases = convertToEntity(orderDto);
        return convertToDto(orderDao.save(purchases));
    }

    @Override
    public void createOrderFromCartId(Long cartId, Principal currentUser) {
        var user = (Account) ((UsernamePasswordAuthenticationToken) currentUser).getPrincipal();
        var cart = cartDao.findById(cartId).orElseThrow(() -> new RuntimeException("Cart not found!"));

        Purchases purchases = new Purchases();
        purchases.setPurchaseAccount(user);
        purchases.setPurchaseDate(LocalDate.now());
        purchases.setTotalOrderPrice(cart.getCartProducts().stream().mapToDouble(Product::getPrice).sum());
        purchases.setPurchaseProducts(cart.getCartProducts());
        convertToDto(orderDao.save(purchases));
    }

    @Override
    public List<OrderDto> findOrdersByCurrentUser(Principal currentUser) {
        var user = (Account) ((UsernamePasswordAuthenticationToken) currentUser).getPrincipal();
        return orderDao.findAllByOrderAccount_Id(user.getId()).stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }
}