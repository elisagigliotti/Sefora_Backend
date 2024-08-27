package it.unical.inf.ea.sefora_backend.service;

import it.unical.inf.ea.sefora_backend.dao.CartDao;
import it.unical.inf.ea.sefora_backend.dao.ProductDao;
import it.unical.inf.ea.sefora_backend.dao.UserDao;
import it.unical.inf.ea.sefora_backend.dto.CartDto;
import it.unical.inf.ea.sefora_backend.dto.CartProductDto;
import it.unical.inf.ea.sefora_backend.dto.OrderDto;
import it.unical.inf.ea.sefora_backend.entities.Cart;
import it.unical.inf.ea.sefora_backend.entities.CartProduct;
import it.unical.inf.ea.sefora_backend.entities.Product;
import it.unical.inf.ea.sefora_backend.entities.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.security.Principal;

@Service
public class CartServiceImpl implements CartService {

    @Autowired
    private CartDao cartDao;

    @Autowired
    private UserDao userDao;

    @Autowired
    private ProductDao productDao;

    @Autowired
    private OrderService orderService;

    @Autowired
    private UserService userService;

    private CartDto convertToDto(Cart cart) {
        CartDto cartDto = new CartDto();

        cartDto.setUserCartId(cart.getUserCart().getId());

        for (CartProduct cartProduct : cart.getCartProducts()) {
            CartProductDto cartProductDto = new CartProductDto();
            cartProductDto.setCartId(cartProduct.getCart().getId());
            cartProductDto.setProductId(cartProduct.getProduct().getId());
            cartProductDto.setQuantity(cartProduct.getQuantity());
            cartProductDto.setId(cartProduct.getId());
            cartDto.getCartProducts().add(cartProductDto);
        }

        cartDto.setId(cart.getId());
        return cartDto;
    }

    private Cart convertToEntity(CartDto cartDto) {
        Cart cart = new Cart();
        User user = userDao.findById(cartDto.getUserCartId()).orElseThrow(() -> new RuntimeException("User not found!"));
        cart.setUserCart(user);

        for (CartProductDto cartProductDto : cartDto.getCartProducts()) {
            CartProduct cartProduct = new CartProduct();
            Cart c1 = cartDao.findById(cartProductDto.getCartId()).orElseThrow(() -> new RuntimeException("Cart not found!"));
            Product p1 = productDao.findById(cartProductDto.getProductId()).orElseThrow(() -> new RuntimeException("Product not found!"));
            cartProduct.setCart(c1);
            cartProduct.setProduct(p1);
            cartProduct.setQuantity(cartProductDto.getQuantity());
            cartProduct.setId(cartProductDto.getId());
            cart.getCartProducts().add(cartProduct);
        }

        cart.setId(cartDto.getId());
        return cart;
    }

    @Override
    public CartDto createCart(CartDto cartDto) {
        if (userDao.findById(cartDto.getId()).isEmpty())
            throw new RuntimeException("User not found!");
        Cart cart = convertToEntity(cartDto);
        return convertToDto(cartDao.save(cart));
    }

    @Override
    public CartDto getCartByUserId(Long id) {
        return cartDao.findByUserCart_Id(id).stream()
                .map(this::convertToDto).findFirst()
                .orElseThrow(() -> new RuntimeException("Cart not found!"));
    }

    @Override
    public CartDto getCurrentUserCart(Principal currentUser) {
        var User = userService.getConnectedUser(currentUser);
        return getCartByUserId(User.getId());
    }

    @Override
    public void updateCart(CartDto cartDto) {
        if (cartDao.findById(cartDto.getId()).isEmpty())
            throw new RuntimeException("Cart not found!");
        cartDao.save(convertToEntity(cartDto));
    }

    @Override
    public CartDto getCartById(Long id) {
        return cartDao.findById(id)
                .map(this::convertToDto)
                .orElseThrow(() -> new RuntimeException("Cart not found!"));
    }

    @Override
    public OrderDto createOrder(OrderDto orderDto) {
        return orderService.createOrder(orderDto);
    }
}
