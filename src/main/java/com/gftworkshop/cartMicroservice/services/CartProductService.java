package com.gftworkshop.cartMicroservice.services;

import com.gftworkshop.cartMicroservice.api.dto.CartProductDto;
import com.gftworkshop.cartMicroservice.model.CartProduct;

import java.util.Optional;

public interface CartProductService {

    int updateQuantity(Long id, int quantity);

    CartProductDto removeProduct(Long id);

}
