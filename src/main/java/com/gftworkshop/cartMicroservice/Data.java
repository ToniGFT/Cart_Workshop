package com.gftworkshop.cartMicroservice;

import com.gftworkshop.cartMicroservice.api.dto.CartDto;

import java.math.BigDecimal;
import java.util.ArrayList;

public class Data {

    public static CartDto createCart001() {
        return CartDto.builder()
                .id(1L)
                .userId(123L)
                .cartProducts(new ArrayList<>())
                .totalPrice(BigDecimal.ZERO)
                .build();
    }

}