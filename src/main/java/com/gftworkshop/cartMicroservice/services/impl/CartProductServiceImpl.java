package com.gftworkshop.cartMicroservice.services.impl;

import com.gftworkshop.cartMicroservice.api.dto.Product;
import com.gftworkshop.cartMicroservice.model.CartProduct;
import com.gftworkshop.cartMicroservice.repositories.CartProductRepository;
import com.gftworkshop.cartMicroservice.services.CartProductService;

public class CartProductServiceImpl implements CartProductService {

    private CartProductRepository cartProductRepository;

    public CartProductServiceImpl(CartProductRepository cartProductRepository) {
        this.cartProductRepository = cartProductRepository;
    }

    @Override
    public void addProduct(Product product) {
        CartProduct cartProduct = new CartProduct();

        cartProduct.setProductName(product.getName());
        cartProductRepository.save(cartProduct);
    }

    @Override
    public void updateQuantity(Long id, int quantity) {

    }

    @Override
    public CartProduct removeProduct(Long id) {
        return null;
    }
}
