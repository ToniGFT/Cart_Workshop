package com.gftworkshop.cartMicroservice.services;

import com.gftworkshop.cartMicroservice.api.dto.CartDto;
import com.gftworkshop.cartMicroservice.model.Cart;
import com.gftworkshop.cartMicroservice.model.CartProduct;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

public interface CartService {
    void addProductToCart(CartProduct cartProduct);

    void removeProductFromCart(CartProduct cartProduct);

    BigDecimal getCartTotal(Long cartId, Long userId);

    void clearCart(Long cartId);

    List<Cart> identifyAbandonedCarts(Date thresholdDate);

    Cart createCart(Long userId);

    CartDto getCart(Long cartId);
}
