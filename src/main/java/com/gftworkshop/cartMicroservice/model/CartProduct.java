package com.gftworkshop.cartMicroservice.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "cart_products")
public class CartProduct {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "cart_id")
    @JsonBackReference
    private Cart cart;

    @NotNull(message = "Product ID cannot be null")
    @Column(name = "product_id")
    private Long productId;

    @NotBlank(message = "Product name cannot be blank")
    @Column(name = "product_name")
    private String productName;

    @NotBlank(message = "Product category cannot be blank")
    @Column(name = "product_category")
    private String productCategory;

    @NotBlank(message = "Product description cannot be blank")
    @Column(name = "product_description")
    private String productDescription;

    @NotNull(message = "Quantity cannot be null")
    @Min(value = 1, message = "Quantity must be at least 1")
    @Column(name = "quantity")
    private Integer quantity;

    @NotNull(message = "Price cannot be null")
    @Column(name = "price", precision = 10, scale = 2)
    private BigDecimal price;
}
