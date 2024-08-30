package it.unical.inf.ea.sefora_backend.service;

import it.unical.inf.ea.sefora_backend.dao.AccountDao;
import it.unical.inf.ea.sefora_backend.dao.CartDao;
import it.unical.inf.ea.sefora_backend.dao.ProductDao;
import it.unical.inf.ea.sefora_backend.dao.PurchaseDao;
import it.unical.inf.ea.sefora_backend.dto.ProductShortDto;
import it.unical.inf.ea.sefora_backend.dto.PurchaseDto;
import it.unical.inf.ea.sefora_backend.entities.Account;
import it.unical.inf.ea.sefora_backend.entities.Cart;
import it.unical.inf.ea.sefora_backend.entities.Product;
import it.unical.inf.ea.sefora_backend.entities.Purchase;
import jakarta.transaction.Transactional;
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
public class PurchaseServiceImpl implements PurchaseService {

    @Autowired
    private PurchaseDao purchaseDao;

    @Autowired
    private AccountDao accountDao;

    @Autowired
    private ProductDao productDao;

    @Autowired
    private CartDao cartDao;

    private Purchase convertToEntity(PurchaseDto purchaseDto) {
        Purchase purchase = new Purchase();
        purchase.setPurchaseProducts(new ArrayList<>());
        Account account = accountDao.findById(purchaseDto.getUserPurchaseId()).orElseThrow(() -> new RuntimeException("User not found!"));
        purchase.setPurchaseAccount(account);
        purchase.setPurchaseDate(purchaseDto.getPurchaseDate());
        purchase.setTotalPurchasePrice(purchaseDto.getTotalPurchasePrice());

        List<Product> products = new ArrayList<>();
        for (ProductShortDto productShortDto : purchaseDto.getProducts()) {
            Product product = productDao.findById(productShortDto.getId()).orElseThrow(() -> new RuntimeException("Product not found!"));
            products.add(product);
        }

        purchase.setPurchaseProducts(products);
        purchase.setId(purchaseDto.getId());
        return purchase;
    }



    private PurchaseDto convertToDto(Purchase purchase) {
        PurchaseDto dto = new PurchaseDto();
        dto.setId(purchase.getId());
        dto.setUserPurchaseId(purchase.getPurchaseAccount().getId());
        dto.setPurchaseDate(purchase.getPurchaseDate());
        dto.setTotalPurchasePrice(purchase.getTotalPurchasePrice());
        dto.setProducts(purchase.getPurchaseProducts().stream()
                .map(product -> {
                    ProductShortDto productDto = new ProductShortDto();
                    productDto.setId(product.getId());
                    productDto.setName(product.getName());
                    productDto.setPrice(product.getPrice());
                    return productDto;
                })
                .collect(Collectors.toList()));
        return dto;
    }



    @Override
    @Transactional
    public PurchaseDto createOrderFromCartId(Long cartId, Principal currentUser) {
        var user = (Account) ((UsernamePasswordAuthenticationToken) currentUser).getPrincipal();
        Cart cart = cartDao.findByIdWithProducts(cartId)
                .orElseThrow(() -> new RuntimeException("Cart not found with ID: " + cartId));

        // Create a new Purchase entity
        Purchase purchase = new Purchase();
        purchase.setPurchaseAccount(user);
        purchase.setPurchaseDate(LocalDate.now());
        purchase.setTotalPurchasePrice(cart.getCartProducts().stream().mapToDouble(Product::getPrice).sum());

        // Add products to the purchase
        for (Product product : cart.getCartProducts()) {
            purchase.addProduct(product);
        }

        for (Product product : cart.getCartProducts()) {
            product.setCart(null);
        }

        // Save the purchase
        Purchase savedPurchase = purchaseDao.save(purchase);

       //cart.getCartProducts().clear();

        // Optionally, delete the cart if you want to remove it from the database
        //cartDao.delete(cart);

        // Alternatively, if you want to keep the cart but clear it
       //cartDao.save(cart);

        // Log before clearing cart
        System.out.println("Cart before clearing: " + cart.getCartProducts());

        cart.getCartProducts().clear();
        cartDao.save(cart);  // Ensure changes to the cart are saved

        // Log after clearing cart
        System.out.println("Cart after clearing: " + cart.getCartProducts());

        // Convert saved Purchase entity back to PurchaseDto to return
        PurchaseDto purchaseDto = convertToDto(savedPurchase);
        return purchaseDto;
    }




    @Override
    public List<PurchaseDto> findOrdersByUserId(Long id) {
        return purchaseDao.findAllByPurchaseAccount_Id(id).stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Override
    public PurchaseDto createOrder(PurchaseDto purchaseDto, Principal currentUser) {
        var user = (Account) ((UsernamePasswordAuthenticationToken) currentUser).getPrincipal();
        if (!Objects.equals(user.getId(), purchaseDto.getUserPurchaseId()))
            throw new RuntimeException("User not allowed to create order for another user!");

        Purchase purchase = convertToEntity(purchaseDto);

        List<Product> productsToAdd = new ArrayList<>(purchase.getPurchaseProducts());

        purchase.getPurchaseProducts().clear();

        for(Product product : productsToAdd) {
            if(productDao.findById(product.getId()).isEmpty())
                throw new RuntimeException("Product not found!");

            purchase.addProduct(product);
        }

        return convertToDto(purchaseDao.save(purchase));
    }

    @Override
    public List<PurchaseDto> findOrdersByCurrentUser(Principal currentUser) {
        var user = (Account) ((UsernamePasswordAuthenticationToken) currentUser).getPrincipal();
        if(user == null)
            throw new RuntimeException("User not found!");

        var purchases = findOrdersByUserId(user.getId());
        if(purchases.isEmpty())
            throw new RuntimeException("No orders found for user with ID: " + user.getId());

        System.out.println("Purchases: " + purchases);

        return purchases;
    }
}