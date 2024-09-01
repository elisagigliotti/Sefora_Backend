package it.unical.inf.ea.sefora_backend.dao;

import it.unical.inf.ea.sefora_backend.entities.Wishlist;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

@Transactional
public interface WishlistDao extends JpaRepository<Wishlist, Long> {

    @Query("SELECT w FROM Wishlist w LEFT JOIN FETCH w.wishlistProducts WHERE w.wishlistAccount.id = :accountId")
    List<Wishlist> findAllByWishlistAccount_Id(@Param("accountId") Long accountId);

    @Query("SELECT w FROM Wishlist w LEFT JOIN FETCH w.wishlistProducts WHERE w.id = :id")
    Optional<Wishlist> findById(Long id);

    @Query("SELECT w FROM Wishlist w LEFT JOIN FETCH w.wishlistProducts wp JOIN w.sharedWithUsers u WHERE u.id = :id")
    List<Wishlist> findBySharedWithUsers_Id(@Param("id") Long id);

    @Query("SELECT DISTINCT w FROM Wishlist w LEFT JOIN FETCH w.wishlistProducts LEFT JOIN w.sharedWithUsers u WHERE w.wishlistAccount.id = :accountId OR u.id = :accountId")
    List<Wishlist> findAllAccessibleWishlistsByAccountId(Long accountId);

    Optional<Wishlist> findByWishlistAccount_Id(Long accountId);

    Wishlist save(Wishlist wishlist);

    void deleteById(Long id);
}
