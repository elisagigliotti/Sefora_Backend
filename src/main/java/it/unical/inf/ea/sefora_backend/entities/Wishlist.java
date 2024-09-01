package it.unical.inf.ea.sefora_backend.entities;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Entity
@Table(name = "wishlist")
@Getter
@Setter
@NoArgsConstructor
public class Wishlist {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @JsonManagedReference
    @OneToMany(mappedBy = "wishlist", fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    private List<Product> wishlistProducts;

    @JsonBackReference
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "wishlist_account_id", nullable = false)
    private Account wishlistAccount;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "type")
    @Enumerated(EnumType.STRING)
    private WishlistType type;

    @JsonManagedReference
    @ManyToMany
    @JoinTable(
            name = "wishlist_shared_users",
            joinColumns = @JoinColumn(name = "wishlist_id"),
            inverseJoinColumns = @JoinColumn(name = "account_id")
    )
    private List<Account> sharedWithUsers;

    public void addProduct(Product product) {
        wishlistProducts.add(product);
        if (product.getWishlist() != this) {
            product.setWishlist(this);
        }
    }

    public void removeProduct(Product product) {
        wishlistProducts.remove(product);
        if (product.getWishlist() == this) {
            product.setWishlist(null);
        }
    }
}
