package it.unical.inf.ea.sefora_backend.dao;

import it.unical.inf.ea.sefora_backend.entities.WishlistProduct;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface WishlistProductDao extends JpaRepository<WishlistProduct, Long> {
    List<WishlistProduct> findAllByWishlist_Id(Long wishlistId);
}
