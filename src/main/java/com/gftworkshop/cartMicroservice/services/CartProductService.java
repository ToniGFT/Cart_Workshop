package com.gftworkshop.cartMicroservice.services;

import com.gftworkshop.cartMicroservice.api.dto.Product;
import com.gftworkshop.cartMicroservice.model.CartProduct;

import java.util.Optional;

public interface CartProductService {

    CartProduct save(CartProduct cartProduct);
    int updateQuantity(Long id, int quantity);

    void removeProduct(Long id);

}
