package com.gftworkshop.cartmicroservice.services.impl;

import com.gftworkshop.cartmicroservice.exceptions.CartNotFoundException;
import com.gftworkshop.cartmicroservice.api.dto.CartProductDto;
import com.gftworkshop.cartmicroservice.exceptions.CartProductInvalidQuantityException;
import com.gftworkshop.cartmicroservice.exceptions.CartProductNotFoundException;
import com.gftworkshop.cartmicroservice.model.Cart;
import com.gftworkshop.cartmicroservice.model.CartProduct;
import com.gftworkshop.cartmicroservice.repositories.CartProductRepository;
import com.gftworkshop.cartmicroservice.repositories.CartRepository;
import com.gftworkshop.cartmicroservice.services.CartProductService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Slf4j
@Service
public class CartProductServiceImpl implements CartProductService {

    private final CartProductRepository cartProductRepository;
    private final CartRepository cartRepository;

    public CartProductServiceImpl(CartProductRepository cartProductRepository, CartRepository cartRepository) {
        this.cartProductRepository = cartProductRepository;
        this.cartRepository = cartRepository;
    }

    @Override
    public int updateQuantity(Long id, int quantity) {
        if (quantity <= 0) {
            throw new CartProductInvalidQuantityException("The quantity must be higher than 0");
        }
        Optional<Cart> optionalCart = cartRepository.findById(id);
        if (!optionalCart.isPresent()) {
            throw new CartNotFoundException("Cart with ID " + id + " not found");
        }
        log.info("Updating quantity for CartProduct with ID {} to {}", id, quantity);
        int updatedQuantity = cartProductRepository.updateQuantity(id, quantity);
        log.info("Quantity updated successfully for CartProduct with ID {} to {}", id, quantity);
        return updatedQuantity;
    }

    @Override
    public CartProductDto removeProduct(Long id) {
        log.info("Removing CartProduct with ID {}", id);
        return cartProductRepository.findById(id)
                .map(cartProduct -> {
                    cartProductRepository.deleteById(id);
                    return entityToDto(cartProduct);
                })
                .orElseThrow(() -> new CartProductNotFoundException("No se encontró el CartProduct con ID: " + id));
    }

    private CartProductDto entityToDto(CartProduct cartProduct) {
        CartProductDto cartProductDto = CartProductDto.builder().build();
        BeanUtils.copyProperties(cartProduct, cartProductDto);
        return cartProductDto;
    }
}
