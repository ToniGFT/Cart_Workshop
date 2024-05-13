package com.gftworkshop.cartMicroservice.services.impl;

import com.gftworkshop.cartMicroservice.exceptions.CartProductSaveException;
import com.gftworkshop.cartMicroservice.model.CartProduct;
import com.gftworkshop.cartMicroservice.repositories.CartProductRepository;
import com.gftworkshop.cartMicroservice.services.CartProductService;
import org.springframework.stereotype.Service;


@Service
public class CartProductServiceImpl implements CartProductService {

    private final CartProductRepository cartProductRepository;

    public CartProductServiceImpl(CartProductRepository cartProductRepository) {
        this.cartProductRepository = cartProductRepository;
    }

    @Override
    public CartProduct save(CartProduct cartProduct) throws CartProductSaveException {
        return cartProductRepository.save(cartProduct);
    }

    @Override
    public int updateQuantity(Long id, int quantity) {
        if (quantity <= 0) {
            throw new CartProductSaveException("The quantity must be higher than 0");
        }
        return cartProductRepository.updateProductQuantity(id, quantity);
    }

    @Override
    public void removeProduct(Long id) {
        cartProductRepository.deleteById(id);
    }
}
