package it.unical.inf.ea.sefora_backend.entities;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Table(name = "s_wishlist")
@Data
@NoArgsConstructor
public class Wishlist {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "s_user_wishlist_id", nullable = false)
    private User userWishlist;

    @OneToMany(mappedBy = "wishlist", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<WishlistProduct> wishlistProducts;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "type")
    @Enumerated(EnumType.STRING)
    private WishlistType type;

    @ManyToMany
    @JoinTable(
            name = "s_wishlist_shared_users",
            joinColumns = @JoinColumn(name = "s_wishlist_id"),
            inverseJoinColumns = @JoinColumn(name = "s_users_id")
    )
    private List<User> sharedWithUsers;

    @Column(name = "shareable_link")
    private String shareableLink;
}
