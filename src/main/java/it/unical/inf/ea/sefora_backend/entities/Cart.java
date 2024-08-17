package it.unical.inf.ea.sefora_backend.entities;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Table(name = "s_cart")
@Data
@NoArgsConstructor
public class Cart {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @OneToOne
    @JoinColumn(name = "s_users_id")
    private User userCart;

    @OneToMany(mappedBy = "cart", cascade = CascadeType.REMOVE)
    private List<CartProduct> cartProducts;
}
