package com.gftworkshop.cartMicroservice.services;

import com.gftworkshop.cartMicroservice.api.dto.Product;
import com.gftworkshop.cartMicroservice.model.CartProduct;

public interface CartProductService {

    CartProduct save(CartProduct cartProduct);
    void updateQuantity(Long id, int quantity);

    CartProduct removeProduct(Long id);

}
