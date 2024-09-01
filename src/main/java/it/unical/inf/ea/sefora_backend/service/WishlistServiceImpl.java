package it.unical.inf.ea.sefora_backend.service;

import it.unical.inf.ea.sefora_backend.dao.AccountDao;
import it.unical.inf.ea.sefora_backend.dao.ProductDao;
import it.unical.inf.ea.sefora_backend.dao.WishlistDao;
import it.unical.inf.ea.sefora_backend.dto.AccountShortDto;
import it.unical.inf.ea.sefora_backend.dto.ProductDto;
import it.unical.inf.ea.sefora_backend.dto.WishlistDto;
import it.unical.inf.ea.sefora_backend.entities.Account;
import it.unical.inf.ea.sefora_backend.entities.Product;
import it.unical.inf.ea.sefora_backend.entities.Wishlist;
import it.unical.inf.ea.sefora_backend.entities.WishlistType;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
public class WishlistServiceImpl implements WishlistService {

    @Autowired
    private WishlistDao wishlistDao;

    @Autowired
    private AccountDao accountDao;

    @Autowired
    private ProductDao productDao;

    @Autowired
    private AccountDao userDao;

    @Transactional
    protected WishlistDto convertToDto(Wishlist wishlist) {
        WishlistDto wishlistDto = new WishlistDto();
        wishlistDto.setId(wishlist.getId());
        wishlistDto.setName(wishlist.getName());
        wishlistDto.setType(wishlist.getType());

        if(accountDao.findById(wishlist.getWishlistAccount().getId()).isEmpty())
            throw new RuntimeException("User not found!");
        AccountShortDto owner = AccountShortDto.builder()
                .id(wishlist.getWishlistAccount().getId())
                .email(wishlist.getWishlistAccount().getEmail())
                .firstname(wishlist.getWishlistAccount().getFirstname())
                .profileImage(wishlist.getWishlistAccount().getProfileImage())
                .role(wishlist.getWishlistAccount().getRole())
                .isBanned(wishlist.getWishlistAccount().getBanned())
                .build();

        wishlistDto.setAccount(owner);

        // Convert products to DTO
        List<ProductDto> productDtos = new ArrayList<>();
        for (Product product : wishlist.getWishlistProducts()) {
            if(productDao.findById(product.getId()).isEmpty())
                throw new RuntimeException("Product not found!");

            ProductDto productDto = ProductDto.builder()
                    .id(product.getId())
                    .name(product.getName())
                    .description(product.getDescription())
                    .price(product.getPrice())
                    .userProductId(product.getProductAccount().getId())
                    .category(product.getCategory())
                    .imageProduct(product.getImageProduct())
                    .build();
            productDtos.add(productDto);
        }
        wishlistDto.setProducts(productDtos);

        // Convert shared users to DTO
        List<AccountShortDto> sharedUserDtos = new ArrayList<>();
        for (Account account : wishlist.getSharedWithUsers()) {
            AccountShortDto sharedUserDto = AccountShortDto.builder()
                    .id(account.getId())
                    .email(account.getEmail())
                    .firstname(account.getFirstname())
                    .profileImage(account.getProfileImage())
                    .role(account.getRole())
                    .isBanned(account.getBanned())
                    .build();
            sharedUserDtos.add(sharedUserDto);
        }
        wishlistDto.setSharedWithUsers(sharedUserDtos);
        return wishlistDto;
    }

    @Transactional
    protected Wishlist convertToEntity(WishlistDto wishlistDto) {
        Wishlist wishlist = new Wishlist();
        wishlist.setId(wishlistDto.getId());
        wishlist.setName(wishlistDto.getName());
        wishlist.setType(wishlistDto.getType());
        Account account = accountDao.findById(wishlistDto.getAccount().getId())
                .orElseThrow(() -> new RuntimeException("User not found!"));
        wishlist.setWishlistAccount(account);

        List<Product> products = new ArrayList<>();
        for (ProductDto productDto : wishlistDto.getProducts()) {
            Product product = productDao.findById(productDto.getId())
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
    @Transactional
    public WishlistDto createWishlist(WishlistDto wishlistDto, Principal currentUser) {
        var user = (Account) ((UsernamePasswordAuthenticationToken) currentUser).getPrincipal();
        if (!Objects.equals(wishlistDto.getAccount().getId(), user.getId()))
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

        if (!Objects.equals(wishlistDto.getAccount().getId(), user.getId())) {
            throw new RuntimeException("You are not allowed to update this wishlist!");
        }

        Wishlist existingWishlist = wishlistDao.findById(wishlistDto.getId())
                .orElseThrow(() -> new RuntimeException("Wishlist not found!"));

        // Update the wishlist properties
        existingWishlist.setName(wishlistDto.getName());
        existingWishlist.setType(wishlistDto.getType());

        // Update products
        List<Product> products = new ArrayList<>();
        for (ProductDto productDto : wishlistDto.getProducts()) {
            Product product = productDao.findById(productDto.getId())
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
        if (!wishlist.getWishlistAccount().getId().equals(user.getId()))
            throw new RuntimeException("You are not allowed to delete this wishlist!");


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

    public Wishlist checkifWishlistExists(Account account) {
        Account acc = userDao.findById(account.getId()).orElseThrow(() -> new RuntimeException("User not found!"));
        Optional<Wishlist> wishlist = wishlistDao.findByWishlistAccount_Id(acc.getId());

        if(wishlist.isEmpty()) {
            Wishlist newWishlist = new Wishlist();
            newWishlist.setWishlistAccount(acc);
            newWishlist.setName("Personal Wishlist");
            newWishlist.setType(WishlistType.PERSONAL);
            return wishlistDao.save(newWishlist);
        } else {
            return wishlist.get();
        }
    }

    @Override
    public void addUserToWishlist(Long userId, Principal currentUser) {
        var user = (Account) ((UsernamePasswordAuthenticationToken) currentUser).getPrincipal();

        Wishlist wishlist = checkifWishlistExists(user);

        if (!Objects.equals(wishlist.getWishlistAccount().getId(), user.getId()))
            throw new RuntimeException("You can't add a user to another user's wishlist!");

        Account account = accountDao.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found!"));

        if(wishlist.getSharedWithUsers().contains(account))
            return;

        wishlist.getSharedWithUsers().add(account);

        if(wishlist.getType().equals(WishlistType.PERSONAL)) wishlist.setType(WishlistType.SHARED);

        wishlistDao.save(wishlist);
    }

    @Override
    public void addUserThroughEmailToWishlist(String userEmail, Principal currentUser) {
        var user = (Account) ((UsernamePasswordAuthenticationToken) currentUser).getPrincipal();

        Wishlist wishlist = checkifWishlistExists(user);

        if (!Objects.equals(wishlist.getWishlistAccount().getId(), user.getId()))
            throw new RuntimeException("You can't add a user to another user's wishlist!");

        Account account = accountDao.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("User not found!"));

        if(wishlist.getSharedWithUsers().contains(account))
            return;

        wishlist.getSharedWithUsers().add(account);

        if(wishlist.getType().equals(WishlistType.PERSONAL)) wishlist.setType(WishlistType.SHARED);

        wishlistDao.save(wishlist);
    }

    @Override
    public void removeUserFromWishlist(Long userId, Principal currentUser) {
        var user = (Account) ((UsernamePasswordAuthenticationToken) currentUser).getPrincipal();

        Wishlist wishlist = checkifWishlistExists(user);

        if (!Objects.equals(wishlist.getWishlistAccount().getId(), user.getId())) {
            throw new RuntimeException("You are not allowed to remove a user from this wishlist!");
        }

        Account accountToRemove = accountDao.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found!"));

        if(!wishlist.getSharedWithUsers().contains(accountToRemove)) return;

        wishlist.getSharedWithUsers().remove(accountToRemove);

        if(wishlist.getSharedWithUsers().isEmpty()) wishlist.setType(WishlistType.PERSONAL);

        wishlistDao.save(wishlist);
    }

    @Override
    public void addProductToWishlist(Long productId, Principal currentUser) {
        var user = (Account) ((UsernamePasswordAuthenticationToken) currentUser).getPrincipal();

        Wishlist wishlist = checkifWishlistExists(user);

        Product product = productDao.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found!"));

        if (!Objects.equals(wishlist.getWishlistAccount().getId(), user.getId()))
            throw new RuntimeException("You can't add a product to another user's wishlist!");

        if(!wishlist.getWishlistProducts().isEmpty() && wishlist.getWishlistProducts().contains(product))
            return;

        wishlist.addProduct(product);
        wishlistDao.save(wishlist);
    }

    @Override
    public void removeProductFromWishlist(Long productId, Principal currentUser) {
        var user = (Account) ((UsernamePasswordAuthenticationToken) currentUser).getPrincipal();

        Wishlist wishlist = checkifWishlistExists(user);

        Product product = productDao.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found!"));

        if (!Objects.equals(wishlist.getWishlistAccount().getId(), user.getId())) {
            throw new RuntimeException("You can't remove a product from another user's wishlist!");
        }

        if(!wishlist.getWishlistProducts().isEmpty() && wishlist.getWishlistProducts().contains(product))
            return;

        wishlist.removeProduct(product);
        wishlistDao.save(wishlist);
    }

    @Override
    @Transactional
    public List<WishlistDto> getAllAccessibleWishlists(Principal currentUser) {
        if(currentUser == null)
            throw new RuntimeException("You are not logged in!");

        var user = (Account) ((UsernamePasswordAuthenticationToken) currentUser).getPrincipal();
        var user1 = userDao.findById(user.getId()).orElseThrow(() -> new RuntimeException("User not found!"));
        return wishlistDao.findAllAccessibleWishlistsByAccountId(user1.getId())
                .stream().map(this::convertToDto).toList();
    }
}