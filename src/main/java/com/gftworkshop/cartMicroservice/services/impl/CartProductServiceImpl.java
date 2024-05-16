package com.gftworkshop.cartMicroservice.services.impl;

import com.gftworkshop.cartMicroservice.exceptions.CartProductInvalidQuantityException;
import com.gftworkshop.cartMicroservice.exceptions.CartProductNotFoundException;
import com.gftworkshop.cartMicroservice.model.CartProduct;
import com.gftworkshop.cartMicroservice.repositories.CartProductRepository;
import com.gftworkshop.cartMicroservice.services.CartProductService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class CartProductServiceImpl implements CartProductService {

    private final CartProductRepository cartProductRepository;

    public CartProductServiceImpl(CartProductRepository cartProductRepository) {
        this.cartProductRepository = cartProductRepository;
    }

    @Override
    public int updateQuantity(Long id, int quantity) {
        if (quantity <= 0) {
            throw new CartProductInvalidQuantityException("The quantity must be higher than 0");
        }
        log.info("Updating quantity for CartProduct with ID {} to {}", id, quantity);
        int updatedQuantity = cartProductRepository.updateQuantity(id, quantity);
        log.info("Quantity updated successfully for CartProduct with ID {} to {}", id, quantity);
        return updatedQuantity;
    }

    @Override
    public CartProduct removeProduct(Long id) {
        return cartProductRepository.findById(id)
                .map(cartProduct -> {
                    cartProductRepository.deleteById(id);
                    return cartProduct;
                })
                .orElseThrow(() -> new CartProductNotFoundException("No se encontró el CartProduct con ID: " + id));
    }
}
