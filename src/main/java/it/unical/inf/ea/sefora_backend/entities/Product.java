package it.unical.inf.ea.sefora_backend.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Table(name = "s_product")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "name")
    private String name;

    @Column(name = "description")
    private String description;

    @Column(name = "price")
    private Float price;

    @Column(name = "quantity")
    private Long quantity;

    @Column(name = "category")
    @Enumerated(EnumType.STRING)
    private Category category;

    @Lob
    @Column(name = "imageProduct")
    private String imageProduct;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "s_users_id")
    private User userProduct;

}
