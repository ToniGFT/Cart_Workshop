package com.gftworkshop.cartMicroservice.api.dto;

import com.gftworkshop.cartMicroservice.model.CartProduct;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CartDto {
    private Long id;
    private Long user_id;
    private Date updated_at;
    private List<CartProduct> cartProducts;

}
