package com.gftworkshop.cartMicroservice.api.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@Data
@NoArgsConstructor
public class UpdatedCartProductDto {
    private Long id;
    private Integer quantity;
}
