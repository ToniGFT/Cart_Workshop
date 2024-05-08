package com.gftworkshop.cartMicroservice.api.dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class Product {
    private Long id;
    private String name;
    private String description;
    private Double price;
    private Integer stock;
    private String category;
    private Double discount;

}
