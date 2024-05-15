package com.gftworkshop.cartMicroservice.services;

import com.gftworkshop.cartMicroservice.model.CartProduct;

import java.util.Optional;

public interface CartProductService {

    int updateQuantity(Long id, int quantity);

    CartProduct removeProduct(Long id);

}
