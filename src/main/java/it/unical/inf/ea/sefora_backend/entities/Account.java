package it.unical.inf.ea.sefora_backend.entities;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

@Entity
@Builder
@Getter
@Setter
@Table(name = "account")
@NoArgsConstructor
@AllArgsConstructor
public class Account implements UserDetails {

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
    @OneToMany(mappedBy = "productAccount", cascade = CascadeType.ALL)
    private List<Product> accountProducts;

    @JsonManagedReference
    @OneToOne(mappedBy = "cartAccount", cascade = CascadeType.ALL)
    private Cart accountCart;

    @JsonManagedReference
    @OneToMany(mappedBy = "purchaseAccount", cascade = CascadeType.ALL) // Changed 'orderAccount' to 'purchaseAccount'
    private List<Purchase> accountPurchases;  // Changed 'accountOrders' to 'accountPurchases'

    @JsonManagedReference
    @OneToMany(mappedBy = "wishlistAccount", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Wishlist> accountWishlists;

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
