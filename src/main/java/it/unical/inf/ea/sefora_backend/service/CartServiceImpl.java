package it.unical.inf.ea.sefora_backend.service;

import it.unical.inf.ea.sefora_backend.dao.AccountDao;
import it.unical.inf.ea.sefora_backend.dao.CartDao;
import it.unical.inf.ea.sefora_backend.dao.ProductDao;
import it.unical.inf.ea.sefora_backend.dto.CartDto;
import it.unical.inf.ea.sefora_backend.dto.ProductShortDto;
import it.unical.inf.ea.sefora_backend.dto.PurchaseDto;
import it.unical.inf.ea.sefora_backend.entities.Account;
import it.unical.inf.ea.sefora_backend.entities.Cart;
import it.unical.inf.ea.sefora_backend.entities.Product;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
public class CartServiceImpl implements CartService {

    @Autowired
    private CartDao cartDao;

    @Autowired
    private AccountDao accountDao;

    @Autowired
    private ProductDao productDao;

    @Autowired
    private PurchaseService purchaseService;

    private CartDto convertToDto(Cart cart) {
        CartDto cartDto = new CartDto();

        Long userId = accountDao.findById(cart.getCartAccount().getId()).orElseThrow(() -> new RuntimeException("User not found!")).getId();
        cartDto.setUserCartId(userId);

        List<ProductShortDto> products = new ArrayList<>();

        for (Product product : cart.getCartProducts()) {
            if (productDao.findById(product.getId()).isEmpty())
                throw new RuntimeException("Product not found!");

            ProductShortDto productShortDto = new ProductShortDto();
            productShortDto.setId(product.getId());
            productShortDto.setName(product.getName());
            productShortDto.setPrice(product.getPrice());
            products.add(productShortDto);
        }

        cartDto.setProducts(products);
        cartDto.setId(cart.getId());
        return cartDto;
    }

    private Cart convertToEntity(CartDto cartDto) {
        Cart cart = new Cart();
        cart.setCartProducts(new ArrayList<>());
        Account account = accountDao.findById(cartDto.getUserCartId())
                .orElseThrow(() -> new RuntimeException("User not found!"));
        cart.setCartAccount(account);

        List<Product> products = new ArrayList<>();
        for (ProductShortDto productShortDto : cartDto.getProducts()) {
            Product product = productDao.findById(productShortDto.getId()).orElseThrow(() -> new RuntimeException("Product not found!"));
            products.add(product);
        }

        cart.setCartProducts(products);
        cart.setId(cartDto.getId());
        return cart;
    }

    @Override
    public CartDto createCart(CartDto cartDto, Principal currentUser) {
        var user = (Account) ((UsernamePasswordAuthenticationToken) currentUser).getPrincipal();
        if (!Objects.equals(cartDto.getUserCartId(), user.getId()))
            throw new RuntimeException("You can't create a cart for another user!");

        // Convert DTO to Entity
        Cart cart = convertToEntity(cartDto);

        // Collect the products to be added to a separate list
        List<Product> productsToAdd = new ArrayList<>(cart.getCartProducts());

        // Clear the current product list to avoid concurrent modification issues
        cart.getCartProducts().clear();

        // Ensure the products exist and add them to the cart
        for (Product product : productsToAdd) {
            if (productDao.findById(product.getId()).isEmpty())
                throw new RuntimeException("Product with id " + product.getId() + " not found!");

            // Ensure bidirectional relationship
            cart.addProduct(product);
        }

        // Save the cart
        return convertToDto(cartDao.save(cart));
    }


    @Override
    public CartDto getCurrentUserCart(Principal principal) {
        if (!(principal instanceof UsernamePasswordAuthenticationToken)) {
            throw new RuntimeException("Unexpected principal type!");
        }

        var user = (Account) ((UsernamePasswordAuthenticationToken) principal).getPrincipal();
        if (user == null) {
            throw new RuntimeException("User not found!");
        }

        var cart = getCartByAccountId(user.getId());
        if (cart == null) {
            throw new RuntimeException("Cart not found!");
        }

        return cart;
    }


    @Override
    public void updateCart(CartDto cartDto, Principal currentUser) {
        var user = (Account) ((UsernamePasswordAuthenticationToken) currentUser).getPrincipal();

        if (!Objects.equals(cartDto.getUserCartId(), user.getId()))
            throw new RuntimeException("You can't update another user's cart!");

        if (accountDao.findById(cartDto.getId()).isEmpty())
            throw new RuntimeException("User not found!");

        if (cartDao.findById(cartDto.getId()).isEmpty())
            throw new RuntimeException("Cart not found!");

        Cart updatedCart = convertToEntity(cartDto);
        Cart existingCart = cartDao.findById(cartDto.getId())
                .orElseThrow(() -> new RuntimeException("Cart not found!"));

        // Update existing cart with new values
        existingCart.setCartProducts(updatedCart.getCartProducts());
        existingCart.setCartAccount(updatedCart.getCartAccount());

        cartDao.save(existingCart);
    }

    @Override
    public CartDto getCartDtoById(Long id) {
        return cartDao.findByIdWithProducts(id)
                .map(this::convertToDto)
                .orElseThrow(() -> new RuntimeException("Cart not found!"));
    }

    public Cart getCartById(Long id) {
        return cartDao.findByIdWithProducts(id)
                .orElseThrow(() -> new RuntimeException("Cart not found!"));
    }

    public CartDto getCartByAccountId(Long id) {
        return cartDao.findByUserIdWithProducts(id)
                .map(this::convertToDto)
                .orElseThrow(() -> new RuntimeException("Cart not found!"));
    }

    @Override
    public void addProductToCart(Long cartId, Long productId, Principal currentUser) {
        var user = (Account) ((UsernamePasswordAuthenticationToken) currentUser).getPrincipal();
        Cart cart = cartDao.findById(cartId)
                .orElseThrow(() -> new RuntimeException("Cart not found!"));
        Product product = productDao.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found!"));

        if (!Objects.equals(cart.getCartAccount().getId(), user.getId()))
            throw new RuntimeException("You can't add a product to another user's cart!");

        cart.addProduct(product);
        cartDao.save(cart);
    }

    @Override
    public void removeProductFromCart(Long cartId, Long productId, Principal currentUser) {
        var user = (Account) ((UsernamePasswordAuthenticationToken) currentUser).getPrincipal();
        Cart cart = cartDao.findById(cartId)
                .orElseThrow(() -> new RuntimeException("Cart not found!"));
        Product product = productDao.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found!"));

        if (!Objects.equals(cart.getCartAccount().getId(), user.getId()))
            throw new RuntimeException("You can't remove a product from another user's cart!");

        cart.removeProduct(product);
        cartDao.save(cart);
    }

    @Override
    @Transactional
    public void checkoutCart(Long cartId, Principal currentUser) {
        // Retrieve the currently authenticated user
        var user = (Account) ((UsernamePasswordAuthenticationToken) currentUser).getPrincipal();

        // Find the cart by its ID
        Cart cart = cartDao.findById(cartId)
                .orElseThrow(() -> new RuntimeException("Cart not found with ID: " + cartId));

        // Check if the cart belongs to the current user
        if (!Objects.equals(cart.getCartAccount().getId(), user.getId())) {
            throw new RuntimeException("You can't checkout another user's cart!");
        }

        // Check if the cart is empty
        if (cart.getCartProducts().isEmpty()) {
            throw new RuntimeException("Cannot checkout an empty cart!");
        }

        // Convert cart products to ProductShortDto list for PurchaseDto
        List<ProductShortDto> productShortDtos = new ArrayList<>();
        for (Product product : cart.getCartProducts()) {
            // Ensure each product is loaded in the current Hibernate session
            Product managedProduct = productDao.findById(product.getId())
                    .orElseThrow(() -> new RuntimeException("Product not found with ID: " + product.getId()));

            // Convert to ProductShortDto for the DTO
            ProductShortDto productShortDto = new ProductShortDto();
            productShortDto.setId(managedProduct.getId());
            productShortDto.setName(managedProduct.getName());
            productShortDto.setPrice(managedProduct.getPrice());
            productShortDtos.add(productShortDto);
        }

        // Create an order from the cart
        PurchaseDto purchaseDto = purchaseService.createOrderFromCartId(cartId, currentUser);

        // Verify that products were correctly added to the PurchaseDto
        purchaseDto.setProducts(productShortDtos);

        // Output debugging information
        System.out.println("Checkout complete. Purchase details: " + purchaseDto);
    }

}
