package com.gftworkshop.cartMicroservice.services;

import com.gftworkshop.cartMicroservice.api.dto.CartDto;
import com.gftworkshop.cartMicroservice.model.CartProduct;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public interface CartService {
    void addProductToCart(CartProduct cartProduct);

    BigDecimal getCartTotal(Long cartId, Long userId);

    void clearCart(Long cartId);

    List<CartDto> identifyAbandonedCarts(LocalDate thresholdDate);

    CartDto createCart(Long userId);

    CartDto getCart(Long cartId);
}
