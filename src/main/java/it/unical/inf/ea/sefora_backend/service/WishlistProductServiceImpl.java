package it.unical.inf.ea.sefora_backend.service;

import it.unical.inf.ea.sefora_backend.dao.ProductDao;
import it.unical.inf.ea.sefora_backend.dao.WishlistDao;
import it.unical.inf.ea.sefora_backend.dao.WishlistProductDao;
import it.unical.inf.ea.sefora_backend.dto.WishlistProductDto;
import it.unical.inf.ea.sefora_backend.entities.Product;
import it.unical.inf.ea.sefora_backend.entities.Wishlist;
import it.unical.inf.ea.sefora_backend.entities.WishlistProduct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class WishlistProductServiceImpl implements WishlistProductService {

    @Autowired
    private WishlistProductDao wishlistProductDao;

    @Autowired
    private WishlistDao wishlistDao;

    @Autowired
    private ProductDao productDao;

    private WishlistProductDto convertToDto(WishlistProduct wishlistProduct) {
        WishlistProductDto wishlistProductDto = new WishlistProductDto();
        wishlistProductDto.setId(wishlistProduct.getId());
        wishlistProductDto.setWishlistId(wishlistProduct.getWishlist().getId());
        wishlistProductDto.setProductId(wishlistProduct.getProduct().getId());
        return wishlistProductDto;
    }

    private WishlistProduct convertToEntity(WishlistProductDto wishlistProductDto) {
        WishlistProduct wishlistProduct = new WishlistProduct();
        Wishlist wishlist = wishlistDao.findById(wishlistProductDto.getWishlistId()).orElseThrow(() -> new RuntimeException("Wishlist not found!"));
        Product product = productDao.findById(wishlistProductDto.getProductId()).orElseThrow(() -> new RuntimeException("Product not found!"));
        wishlistProduct.setWishlist(wishlist);
        wishlistProduct.setProduct(product);
        wishlistProduct.setId(wishlistProductDto.getId());
        return wishlistProduct;
    }

    @Override
    public void deleteWishlistProduct(Long id) {
        if (wishlistProductDao.findById(id).isEmpty())
            throw new RuntimeException("WishlistProduct not found!");

        wishlistProductDao.deleteById(id);
    }

    @Override
    public WishlistProductDto createWishlistProduct(WishlistProductDto wishlistProductDto) {
        WishlistProduct wishlistProduct = convertToEntity(wishlistProductDto);
        return convertToDto(wishlistProductDao.save(wishlistProduct));
    }

    @Override
    public List<WishlistProductDto> findAllWishlistProduct(Long id) {
        return wishlistProductDao.findAllByWishlist_Id(id)
                .stream().map(this::convertToDto).toList();
    }
}
