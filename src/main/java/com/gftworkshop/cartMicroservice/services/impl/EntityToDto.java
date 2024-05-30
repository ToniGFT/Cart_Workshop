package com.gftworkshop.cartMicroservice.services.impl;

import com.gftworkshop.cartMicroservice.api.dto.CartDto;
import com.gftworkshop.cartMicroservice.api.dto.CartProductDto;
import com.gftworkshop.cartMicroservice.model.Cart;
import com.gftworkshop.cartMicroservice.model.CartProduct;
import org.springframework.beans.BeanUtils;

import java.util.List;

public class EntityToDto {

    public static CartDto convertCartToDto(Cart cart) {
        CartDto cartDto = CartDto.builder().build();
        BeanUtils.copyProperties(cart, cartDto);
        return cartDto;
    }

    public static CartProductDto convertCartProductToDto(CartProduct cartProduct) {
        CartProductDto cartProductDto = CartProductDto.builder().build();
        BeanUtils.copyProperties(cartProduct, cartProductDto);
        return cartProductDto;
    }

    public static List<Long> getIdList(List<CartProduct> cartProducts) {
        return cartProducts.stream()
                .map(CartProduct::getId)
                .toList();
    }

}
