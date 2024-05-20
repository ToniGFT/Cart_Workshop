package com.gftworkshop.cartmicroservice.services;

import com.gftworkshop.cartmicroservice.api.dto.CartProductDto;

public interface CartProductService {

    int updateQuantity(Long id, int quantity);

    CartProductDto removeProduct(Long id);

}
