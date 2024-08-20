package it.unical.inf.ea.sefora_backend.service;

import it.unical.inf.ea.sefora_backend.dao.ProductDao;
import it.unical.inf.ea.sefora_backend.dao.UserDao;
import it.unical.inf.ea.sefora_backend.dao.WishlistDao;
import it.unical.inf.ea.sefora_backend.dto.UserDto;
import it.unical.inf.ea.sefora_backend.dto.WishlistDto;
import it.unical.inf.ea.sefora_backend.dto.WishlistProductDto;
import it.unical.inf.ea.sefora_backend.entities.User;
import it.unical.inf.ea.sefora_backend.entities.Wishlist;
import it.unical.inf.ea.sefora_backend.entities.WishlistProduct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class WishlistServiceImpl implements WishlistService {

    @Autowired
    private WishlistDao wishlistDao;

    @Autowired
    private UserDao userDao;

    @Autowired
    private ProductDao productDao;

    private WishlistDto convertToDto(Wishlist wishlist) {
        WishlistDto wishlistDto = new WishlistDto();
        if (userDao.findById(wishlist.getUserWishlist().getId()).isEmpty())
            throw new RuntimeException("User not found!");
        wishlistDto.setUserWishlistId(wishlist.getUserWishlist().getId());

        for (WishlistProduct wishlistProduct : wishlist.getWishlistProducts()) {
            WishlistProductDto wishlistProductDto = new WishlistProductDto();
            wishlistProductDto.setProductId(wishlistProduct.getProduct().getId());
            wishlistDto.getWishlistProducts().add(wishlistProductDto);
        }

        wishlistDto.setName(wishlist.getName());
        wishlistDto.setType(wishlist.getType());

        for (User u : wishlist.getSharedWithUsers()) {
            UserDto userDto = new UserDto();
            userDto.setFirstname(u.getFirstname());
            userDto.setLastname(u.getLastname());
            userDto.setEmail(u.getEmail());
            userDto.setPhone(u.getPhone());
            userDto.setProfileImage(u.getProfileImage());
            wishlistDto.getSharedWithUsers().add(userDto);
        }

        wishlistDto.setShareableLink(wishlist.getShareableLink());
        return wishlistDto;
    }

    private Wishlist convertToEntity(WishlistDto wishlistDto) {
        Wishlist wishlist = new Wishlist();
        User user = userDao.findById(wishlistDto.getUserWishlistId())
                .orElseThrow(() -> new RuntimeException("User not found!"));
        wishlist.setUserWishlist(user);

        for (WishlistProductDto wishlistProductDto : wishlistDto.getWishlistProducts()) {
            WishlistProduct wishlistProduct = new WishlistProduct();
            wishlistProduct.setProduct(productDao.findById(wishlistProductDto.getProductId())
                    .orElseThrow(() -> new RuntimeException("Product not found!")));
            wishlist.getWishlistProducts().add(wishlistProduct);
        }

        wishlist.setName(wishlistDto.getName());
        wishlist.setType(wishlistDto.getType());

        wishlist.setSharedWithUsers(userDao.findAllById(
                wishlistDao.findBySharedWithUsers_Id(
                        wishlistDto.getSharedWithUsers()
                                .stream().map(UserDto::getId).toList())));

        wishlist.setShareableLink(wishlistDto.getShareableLink());
        return wishlist;
    }

    @Override
    public WishlistDto createWishlist(WishlistDto wishlistDto) {
        wishlistDao.save(convertToEntity(wishlistDto));
        return wishlistDto;
    }

    @Override
    public void updateWishlist(WishlistDto wishlistDto) {
        Wishlist existingWishlist = wishlistDao.findById(wishlistDto.getId())
                .orElseThrow(() -> new RuntimeException("Wishlist not found!"));
        existingWishlist.setName(wishlistDto.getName());
        existingWishlist.setType(wishlistDto.getType());
        existingWishlist.setShareableLink(wishlistDto.getShareableLink());
        existingWishlist.setSharedWithUsers(userDao.findAllById(
                wishlistDao.findBySharedWithUsers_Id(
                        wishlistDto.getSharedWithUsers()
                                .stream().map(UserDto::getId).toList())));
        existingWishlist.setWishlistProducts(wishlistDto.getWishlistProducts()
                .stream().map(wishlistProductDto -> {
                    WishlistProduct wishlistProduct = new WishlistProduct();
                    wishlistProduct.setProduct(productDao.findById(wishlistProductDto.getProductId())
                            .orElseThrow(() -> new RuntimeException("Product not found!")));
                    return wishlistProduct;
                }).toList());
        existingWishlist.setId(wishlistDto.getId());
        wishlistDao.save(existingWishlist);
    }

    @Override
    public void deleteWishlist(Long id) {
        getWishlistById(id);
        wishlistDao.deleteById(id);
    }

    @Override
    public WishlistDto getWishlistById(Long id) {
        if (wishlistDao.findById(id).isEmpty()) {
            throw new RuntimeException("Wishlist not found!");
        }
        return convertToDto(wishlistDao.findById(id).get());
    }

    @Override
    public List<WishlistDto> getWishlistsByOwner(Long id) {
        return wishlistDao.findAllByUserWishlist_Id(id)
                .stream().map(this::convertToDto).toList();
    }
}
