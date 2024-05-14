package com.gftworkshop.cartMicroservice.services;

import com.gftworkshop.cartMicroservice.model.CartProduct;

import java.util.Optional;

public interface CartProductService {

    CartProduct save(CartProduct cartProduct);
    int updateQuantity(Long id, int quantity);

    CartProduct removeProduct(Long id);

}
