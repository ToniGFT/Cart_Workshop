package com.gftworkshop.cartMicroservice.api.dto;

import com.gftworkshop.cartMicroservice.model.CartProduct;
import lombok.Builder;
import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
@Builder
public class CartDto {
    private Long id;
    private Long user_id;
    private Date updated_at;
    private List<CartProduct> cartProducts;
}
