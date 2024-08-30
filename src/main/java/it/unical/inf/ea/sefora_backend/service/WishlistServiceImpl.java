package it.unical.inf.ea.sefora_backend.service;

import it.unical.inf.ea.sefora_backend.dao.ProductDao;
import it.unical.inf.ea.sefora_backend.dao.AccountDao;
import it.unical.inf.ea.sefora_backend.dao.WishlistDao;
import it.unical.inf.ea.sefora_backend.dto.ProductShortDto;
import it.unical.inf.ea.sefora_backend.dto.AccountShortDto;
import it.unical.inf.ea.sefora_backend.dto.WishlistDto;
import it.unical.inf.ea.sefora_backend.entities.Account;
import it.unical.inf.ea.sefora_backend.entities.Product;
import it.unical.inf.ea.sefora_backend.entities.Wishlist;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
public class WishlistServiceImpl implements WishlistService {

    @Autowired
    private WishlistDao wishlistDao;

    @Autowired
    private AccountDao accountDao;

    @Autowired
    private ProductDao productDao;


    private WishlistDto convertToDto(Wishlist wishlist) {
        WishlistDto wishlistDto = new WishlistDto();
        wishlistDto.setId(wishlist.getId());
        wishlistDto.setName(wishlist.getName());
        wishlistDto.setType(wishlist.getType());
        wishlistDto.setUserWishlistId(wishlist.getWishlistAccount().getId());

        // Convert products to DTO
        List<ProductShortDto> productDtos = new ArrayList<>();
        for (Product product : wishlist.getWishlistProducts()) {
            if(productDao.findById(product.getId()).isEmpty())
                throw new RuntimeException("Product not found!");

            ProductShortDto productShortDto = new ProductShortDto();
            productShortDto.setId(product.getId());
            productShortDto.setName(product.getName());
            productShortDto.setPrice(product.getPrice());
            productDtos.add(productShortDto);
        }
        wishlistDto.setProducts(productDtos);

        // Convert shared users to DTO
        List<AccountShortDto> sharedUserDtos = new ArrayList<>();
        for (Account account : wishlist.getSharedWithUsers()) {
            AccountShortDto accountShortDto = new AccountShortDto();
            accountShortDto.setId(account.getId());
            accountShortDto.setEmail(account.getEmail());
            accountShortDto.setFirstname(account.getFirstname());
            sharedUserDtos.add(accountShortDto);
        }
        wishlistDto.setSharedWithUsers(sharedUserDtos);

        return wishlistDto;
    }

    private Wishlist convertToEntity(WishlistDto wishlistDto) {
        Wishlist wishlist = new Wishlist();
        wishlist.setId(wishlistDto.getId());
        wishlist.setName(wishlistDto.getName());
        wishlist.setType(wishlistDto.getType());
        Account account = accountDao.findById(wishlistDto.getUserWishlistId())
                .orElseThrow(() -> new RuntimeException("User not found!"));
        wishlist.setWishlistAccount(account);

        List<Product> products = new ArrayList<>();
        for (ProductShortDto productShortDto : wishlistDto.getProducts()) {
            Product product = productDao.findById(productShortDto.getId())
                    .orElseThrow(() -> new RuntimeException("Product not found!"));
            products.add(product);
        }
        wishlist.setWishlistProducts(products);

        List<Account> sharedUsers = new ArrayList<>();
        for (AccountShortDto accountShortDto : wishlistDto.getSharedWithUsers()) {
            Account sharedUser = accountDao.findById(accountShortDto.getId())
                    .orElseThrow(() -> new RuntimeException("User not found!"));
            sharedUsers.add(sharedUser);
        }
        wishlist.setSharedWithUsers(sharedUsers);

        return wishlist;
    }

    @Override
    public WishlistDto createWishlist(WishlistDto wishlistDto, Principal currentUser) {
        var user = (Account) ((UsernamePasswordAuthenticationToken) currentUser).getPrincipal();
        if (!Objects.equals(wishlistDto.getUserWishlistId(), user.getId()))
            throw new RuntimeException("You are not allowed to create a wishlist for another user!");

        Wishlist wishlist = convertToEntity(wishlistDto);

        List<Product> productsToAdd = new ArrayList<>(wishlist.getWishlistProducts());

        System.out.println("Products to add: " + productsToAdd);

        wishlist.getWishlistProducts().clear();

        for (Product product : productsToAdd) {
            if(productDao.findById(product.getId()).isEmpty())
                throw new RuntimeException("Product not found!");

            wishlist.addProduct(product);
        }
        return convertToDto(wishlistDao.save(wishlist));
    }

    @Override
    @Transactional
    public void updateWishlist(WishlistDto wishlistDto, Principal currentUser) {
        var user = (Account) ((UsernamePasswordAuthenticationToken) currentUser).getPrincipal();

        if (!Objects.equals(wishlistDto.getUserWishlistId(), user.getId())) {
            throw new RuntimeException("You are not allowed to update this wishlist!");
        }

        Wishlist existingWishlist = wishlistDao.findById(wishlistDto.getId())
                .orElseThrow(() -> new RuntimeException("Wishlist not found!"));

        // Update the wishlist properties
        existingWishlist.setName(wishlistDto.getName());
        existingWishlist.setType(wishlistDto.getType());

        // Update products
        List<Product> products = new ArrayList<>();
        for (ProductShortDto productShortDto : wishlistDto.getProducts()) {
            Product product = productDao.findById(productShortDto.getId())
                    .orElseThrow(() -> new RuntimeException("Product not found!"));
            products.add(product);
        }
        existingWishlist.setWishlistProducts(products);

        // Update shared users
        List<Account> sharedWithUsers = accountDao.findAllById(
                wishlistDto.getSharedWithUsers().stream().map(AccountShortDto::getId).toList());
        existingWishlist.setSharedWithUsers(sharedWithUsers);

        wishlistDao.save(existingWishlist);
    }

    @Override
    public void deleteWishlist(Long id, Principal currentUser) {
        Wishlist wishlist = wishlistDao.findById(id)
                .orElseThrow(() -> new RuntimeException("Wishlist not found!"));

        var user = (Account) ((UsernamePasswordAuthenticationToken) currentUser).getPrincipal();
        if (!wishlist.getWishlistAccount().getId().equals(user.getId())) {
            throw new RuntimeException("You are not allowed to delete this wishlist!");
        }

        wishlistDao.deleteById(id);
    }

    @Override
    @Transactional
    public WishlistDto getWishlistById(Long id) {
        Wishlist wishlist = wishlistDao.findById(id)
                .orElseThrow(() -> new RuntimeException("Wishlist not found!"));
        return convertToDto(wishlist);
    }

    @Override
    public List<WishlistDto> getCurrentUserWishlist(Principal currentUser) {
        var user = (Account) ((UsernamePasswordAuthenticationToken) currentUser).getPrincipal();
        return wishlistDao.findAllByWishlistAccount_Id(user.getId())
                .stream().map(this::convertToDto).toList();
    }

    @Override
    public List<WishlistDto> getSharedWishlists(Principal currentUser) {
        var user = (Account) ((UsernamePasswordAuthenticationToken) currentUser).getPrincipal();
        return wishlistDao.findBySharedWithUsers_Id(user.getId())
                .stream().map(this::convertToDto).toList();
    }

    @Override
    public void addUserToWishlist(Long wishlistId, Long userId, Principal currentUser) {
        var user = (Account) ((UsernamePasswordAuthenticationToken) currentUser).getPrincipal();
        Wishlist wishlist = wishlistDao.findById(wishlistId)
                .orElseThrow(() -> new RuntimeException("Wishlist not found!"));

        if (!Objects.equals(wishlist.getWishlistAccount().getId(), user.getId())) {
            throw new RuntimeException("You can't add a user to another user's wishlist!");
        }

        Account account = accountDao.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found!"));

        wishlist.getSharedWithUsers().add(account);
        wishlistDao.save(wishlist);
    }

    @Override
    public void removeUserFromWishlist(Long wishlistId, Long userId, Principal currentUser) {
        var user = (Account) ((UsernamePasswordAuthenticationToken) currentUser).getPrincipal();
        Wishlist wishlist = wishlistDao.findById(wishlistId)
                .orElseThrow(() -> new RuntimeException("Wishlist not found!"));

        if (!Objects.equals(wishlist.getWishlistAccount().getId(), user.getId())) {
            throw new RuntimeException("You are not allowed to remove a user from this wishlist!");
        }

        Account accountToRemove = accountDao.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found!"));

        wishlist.getSharedWithUsers().remove(accountToRemove);
        wishlistDao.save(wishlist);
    }

    @Override
    public void addProductToWishlist(Long wishlistId, Long productId, Principal currentUser) {
        var user = (Account) ((UsernamePasswordAuthenticationToken) currentUser).getPrincipal();
        Wishlist wishlist = wishlistDao.findById(wishlistId)
                .orElseThrow(() -> new RuntimeException("Wishlist not found!"));
        Product product = productDao.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found!"));

        if (!Objects.equals(wishlist.getWishlistAccount().getId(), user.getId())) {
            throw new RuntimeException("You can't add a product to another user's wishlist!");
        }

        wishlist.addProduct(product);
        wishlistDao.save(wishlist);
    }

    @Override
    public void removeProductFromWishlist(Long wishlistId, Long productId, Principal currentUser) {
        var user = (Account) ((UsernamePasswordAuthenticationToken) currentUser).getPrincipal();
        Wishlist wishlist = wishlistDao.findById(wishlistId)
                .orElseThrow(() -> new RuntimeException("Wishlist not found!"));
        Product product = productDao.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found!"));

        if (!Objects.equals(wishlist.getWishlistAccount().getId(), user.getId())) {
            throw new RuntimeException("You can't remove a product from another user's wishlist!");
        }

        wishlist.removeProduct(product);
        wishlistDao.save(wishlist);
    }
}