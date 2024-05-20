package com.gftworkshop.cartmicroservice.api.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdatedCartProductDto {
    private Long id;
    private Integer quantity;
}
