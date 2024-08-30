package it.unical.inf.ea.sefora_backend.entities;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "cart")
@Getter
@Setter
@NoArgsConstructor
public class Cart {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JsonManagedReference
    @OneToMany(mappedBy = "cart", fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    private List<Product> cartProducts;

    @JsonBackReference
    @OneToOne
    @JoinColumn(name = "account_id", nullable = false)
    private Account cartAccount;

    // Helper method to add product
    public void addProduct(Product product) {
        cartProducts.add(product);
        if (product.getCart() != this) {
            product.setCart(this);
        }
    }

    // Helper method to remove product
    public void removeProduct(Product product) {
        cartProducts.remove(product);
        if (product.getCart() == this) {
            product.setCart(null);
        }
    }
}
