package com.gftworkshop.cartMicroservice.api.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CartProductDto {
    private Long id;
    private String productName;
    private String productCategory;
    private String productDescription;
    private Integer quantity;
    private BigDecimal price;
}
