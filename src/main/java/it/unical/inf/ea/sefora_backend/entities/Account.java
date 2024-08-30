package it.unical.inf.ea.sefora_backend.entities;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "s_users")
@ToString(exclude = {"cart", "wishlists", "orders", "products"})
public class User implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "firstname")
    private String firstname;

    @Column(name = "lastname")
    private String lastname;

    @Column(name = "email", unique = true)
    private String email;

    @Column(name = "password")
    private String password;

    @Column(name = "role")
    @Enumerated(EnumType.STRING)
    private Role role;

    @Column(name = "phone")
    private String phone;

    @Column(name = "banned")
    private Boolean banned;

    @Lob
    @Column(name = "profile_image")
    private String profileImage;

    @JsonManagedReference
    @OneToMany(mappedBy = "userProduct", cascade = CascadeType.ALL)
    private List<Product> products;

    @JsonManagedReference
    @OneToOne(mappedBy = "userCart", cascade = CascadeType.ALL)
    private Cart cart;

    @JsonManagedReference
    @OneToMany(mappedBy = "userOrder", cascade = CascadeType.ALL)
    private List<Order> orders;

    @JsonManagedReference
    @ManyToMany(mappedBy = "sharedWithUsers")
    private List<Wishlist> wishlists;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return role.getAuthorities();
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return UserDetails.super.isAccountNonExpired() && !banned;
    }

    @Override
    public boolean isAccountNonLocked() {
        return UserDetails.super.isAccountNonLocked() && !banned;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return UserDetails.super.isCredentialsNonExpired() && !banned;
    }

    @Override
    public boolean isEnabled() {
        return UserDetails.super.isEnabled() && !banned;
    }


}
