package it.unical.inf.ea.sefora_backend.dao;

import it.unical.inf.ea.sefora_backend.entities.Wishlist;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface WishlistDao extends JpaRepository<Wishlist, Long> {
    List<Wishlist> findAllByUserWishlist_Id(Long userId);

    Optional<Wishlist> findById(Long id);

    Iterable<Long> findBySharedWithUsers_Id(List<Long> list);
}
