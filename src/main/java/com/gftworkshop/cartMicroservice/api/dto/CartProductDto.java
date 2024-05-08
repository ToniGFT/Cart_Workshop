package com.gftworkshop.cartMicroservice.api.dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class CartProductDto {
    private Long id;
    private String productName;
    private String productCategory;
    private String productDescription;
    private Integer quantity;
    private BigDecimal price;


}
