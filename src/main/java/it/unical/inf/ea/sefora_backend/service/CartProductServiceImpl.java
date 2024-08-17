package it.unical.inf.ea.sefora_backend.service;

import it.unical.inf.ea.sefora_backend.dao.CartDao;
import it.unical.inf.ea.sefora_backend.dao.CartProductDao;
import it.unical.inf.ea.sefora_backend.dao.ProductDao;
import it.unical.inf.ea.sefora_backend.dto.CartProductDto;
import it.unical.inf.ea.sefora_backend.entities.Cart;
import it.unical.inf.ea.sefora_backend.entities.CartProduct;
import it.unical.inf.ea.sefora_backend.entities.Product;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CartProductServiceImpl implements CartProductService {

    @Autowired
    private CartDao cartDao;

    @Autowired
    private CartProductDao cartProductDao;

    @Autowired
    private ModelMapper modelMapper;
    
    @Autowired
    private ProductDao productDao;

    private CartProductDto convertToDto(CartProduct cartProduct) {
        CartProductDto cartProductDto = new CartProductDto();
        cartProductDto.setCartId(cartProduct.getCart().getId());
        cartProductDto.setProductId(cartProduct.getProduct().getId());
        cartProductDto.setQuantity(cartProduct.getQuantity());
        cartProductDto.setId(cartProduct.getId());
        return cartProductDto;
    }

    private CartProduct convertToEntity(CartProductDto cartProductDto) {
        CartProduct cartProduct = new CartProduct();
        Cart cart = cartDao.findById(cartProductDto.getCartId()).orElseThrow(() -> new RuntimeException("Cart not found!"));
        cartProduct.setCart(cart);

        Product product = productDao.findById(cartProductDto.getProductId()).orElseThrow(() -> new RuntimeException("Product not found!"));
        cartProduct.setProduct(product);
        cartProduct.setQuantity(cartProductDto.getQuantity());

        cartProduct.setId(cartProductDto.getId());

        return cartProduct;
    }

    @Override
    public CartProductDto createCartProduct(CartProductDto cartProductDto) {
        if (cartDao.findById(cartProductDto.getCartId()).isEmpty())
            throw new RuntimeException("Cart not found!");

        CartProduct cartProduct = convertToEntity(cartProductDto);

        return convertToDto(cartProductDao.save(cartProduct));
    }

    @Override
    public void updateCartProduct(CartProductDto cartProductDto) {
        if (cartProductDao.findById(cartProductDto.getId()).isEmpty())
            throw new RuntimeException("CartProduct not found!");

        if (cartDao.findById(cartProductDto.getCartId()).isEmpty())
            throw new RuntimeException("Cart not found!");

        cartProductDao.save(convertToEntity(cartProductDto));
    }

    @Override
    public void deleteCartProduct(Long id) {
        cartProductDao.deleteById(id);
    }

    @Override
    public List<CartProductDto> getAllCartProductByCartId(Long id) {
        return cartProductDao.findByCart_Id(id)
                .stream().map(this::convertToDto).collect(Collectors.toList());
    }
}
