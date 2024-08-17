package it.unical.inf.ea.sefora_backend.entities;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Entity
@Data
@Table(name = "s_order")
@NoArgsConstructor
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JsonBackReference
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "s_users_id")
    private User userOrder;

    @Column(name = "purchase_date")
    private LocalDate purchaseDate;

    @Column(name = "total_order_price")
    private double totalOrderPrice;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrderProduct> orderProducts;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "s_address_id", referencedColumnName = "id")
    private Address address;
}
