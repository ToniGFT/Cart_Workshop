package com.gftworkshop.cartMicroservice.model;

import jakarta.persistence.*;
import java.math.BigDecimal;


@Entity
@Table(name = "cart_products")
public class CartProduct {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "cart_id", referencedColumnName = "id")
    private Cart cart;

    @Column(name = "product_name")
    private String productName;

    @Column(name = "product_category")
    private String productCategory;

    @Column(name = "product_description")
    private String productDescription;

    @Column(name = "quantity")
    private Integer quantity;

    @Column(name = "price", precision = 10, scale = 2)
    private BigDecimal price;

    public CartProduct() {
    }

    public CartProduct(Long id, Cart cart, String productName, String productCategory, String productDescription, Integer quantity, BigDecimal price) {
        this.id = id;
        this.cart = cart;
        this.productName = productName;
        this.productCategory = productCategory;
        this.productDescription = productDescription;
        this.quantity = quantity;
        this.price = price;
    }
}
