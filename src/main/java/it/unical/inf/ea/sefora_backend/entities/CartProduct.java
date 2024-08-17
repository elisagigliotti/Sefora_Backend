package it.unical.inf.ea.sefora_backend.entities;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "s_cart_products")
@Data
@NoArgsConstructor
public class CartProduct {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "s_cart_id")
    private Cart cart;

    @ManyToOne
    @JoinColumn(name = "s_product_id")
    private Product product;

    @Column(name = "quantity", nullable = false)
    private Integer quantity;
}
