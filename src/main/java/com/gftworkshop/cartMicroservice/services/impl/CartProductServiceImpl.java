package com.gftworkshop.cartMicroservice.services.impl;

import com.gftworkshop.cartMicroservice.api.dto.Product;
import com.gftworkshop.cartMicroservice.model.Cart;
import com.gftworkshop.cartMicroservice.model.CartProduct;
import com.gftworkshop.cartMicroservice.repositories.CartProductRepository;
import com.gftworkshop.cartMicroservice.services.CartProductService;

import java.util.Optional;

public class CartProductServiceImpl implements CartProductService {

    private CartProductRepository cartProductRepository;

    public CartProductServiceImpl(CartProductRepository cartProductRepository) {
        this.cartProductRepository = cartProductRepository;
    }

    @Override
    public CartProduct save(CartProduct cartProduct) {
        return cartProductRepository.save(cartProduct);
    }

    @Override
    public int updateQuantity(Long id, int quantity) {
        return cartProductRepository.updateProductQuantity(id, quantity);
    }

    @Override
    public void removeProduct(Long id) {
        cartProductRepository.deleteById(id);
    }
}
