package it.unical.inf.ea.sefora_backend.entities;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import it.unical.inf.ea.sefora_backend.entities.token.Token;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "s_users")
public class User implements UserDetails {

    @Id
    @GeneratedValue
    @Column(name = "id")
    private Long id;

    @Column(name = "firstname")
    private String firstname;

    @Column(name = "lastname")
    private String lastname;

    @Column(name = "email")
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

    @OneToMany(mappedBy = "user")
    private List<Token> tokens;

    //Mapping carrello
    @JsonManagedReference
    @OneToOne(mappedBy = "userCart", cascade = CascadeType.REMOVE)
    private Cart cart;

    //Mapping wishlist
    @JsonManagedReference
    @ManyToMany(mappedBy = "sharedWithUsers")
    private List<Wishlist> wishlists;

    //Mapping ordini
    @JsonManagedReference
    @OneToMany(mappedBy = "userOrder", fetch = FetchType.LAZY, cascade = CascadeType.REMOVE)
    private List<Order> orders;

    //Mapping indirizzo
    @JsonManagedReference
    @OneToOne(mappedBy = "userAddress", cascade = CascadeType.REMOVE)
    private Address address;

    //Mapping prodotti
    @JsonManagedReference
    @OneToMany(mappedBy = "userProduct", fetch = FetchType.LAZY, cascade = CascadeType.REMOVE)
    private List<Product> products;

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
