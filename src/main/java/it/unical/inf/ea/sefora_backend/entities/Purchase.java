package it.unical.inf.ea.sefora_backend.entities;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "purchase") // Renamed table to 'purchases'
@Data
@NoArgsConstructor
public class Purchase {  // Renamed class from 'Order' to 'Purchases'

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JsonManagedReference
    @OneToMany(mappedBy = "productPurchase", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<Product> purchaseProducts= new ArrayList<>(); ;  // Renamed from 'orderProducts' to 'purchaseProducts'

    @JsonBackReference
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "account_id", nullable = false)
    private Account purchaseAccount;  // Renamed from 'orderAccount' to 'purchaseAccount'

    @Column(name = "purchase_date")
    private LocalDate purchaseDate;

    @Column(name = "total_purchase_price") // You might want to rename this to 'total_purchase_price'
    private double totalPurchasePrice;

    // Helper method to add product
    public void addProduct(Product product) {
        purchaseProducts.add(product);
        if (product.getProductPurchase() != this) {
            product.setProductPurchase(this);
        }
    }

    // Helper method to remove product
    public void removeProduct(Product product) {
        purchaseProducts.remove(product);
        if (product.getProductPurchase() == this) {
            product.setProductPurchase(null);
        }
    }
}