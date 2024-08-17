package it.unical.inf.ea.sefora_backend.service;

import it.unical.inf.ea.sefora_backend.dao.UserDao;
import it.unical.inf.ea.sefora_backend.dao.WishlistDao;
import it.unical.inf.ea.sefora_backend.dto.UserDto;
import it.unical.inf.ea.sefora_backend.dto.WishlistDto;
import it.unical.inf.ea.sefora_backend.dto.WishlistProductDto;
import it.unical.inf.ea.sefora_backend.entities.User;
import it.unical.inf.ea.sefora_backend.entities.Wishlist;
import it.unical.inf.ea.sefora_backend.entities.WishlistProduct;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class WishlistServiceImpl implements WishlistService {

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private WishlistDao wishlistDao;

    @Autowired
    private UserDao userDao;

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

    @Override
    public WishlistDto createWishlist(WishlistDto wishlistDto) {
        wishlistDao.save(modelMapper.map(wishlistDto, Wishlist.class));
        return wishlistDto;
    }

    @Override
    public void updateWishlist(WishlistDto wishlistDto) {
        Wishlist existingWishlist = wishlistDao.findById(wishlistDto.getId())
                .orElseThrow(() -> new RuntimeException("Wishlist not found!"));
        modelMapper.map(wishlistDto, existingWishlist);
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
