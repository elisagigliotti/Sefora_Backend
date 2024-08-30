package it.unical.inf.ea.sefora_backend.entities;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Entity
@Table(name = "purchases") // Renamed table to 'purchases'
@Data
@NoArgsConstructor
public class Purchases {  // Renamed class from 'Order' to 'Purchases'

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JsonManagedReference
    @OneToMany(mappedBy = "productPurchase", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<Product> purchaseProducts;  // Renamed from 'orderProducts' to 'purchaseProducts'

    @JsonBackReference
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "account_id", nullable = false)
    private Account purchaseAccount;  // Renamed from 'orderAccount' to 'purchaseAccount'

    @Column(name = "purchase_date")
    private LocalDate purchaseDate;

    @Column(name = "total_order_price") // You might want to rename this to 'total_purchase_price'
    private double totalOrderPrice;
}