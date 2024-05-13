package com.gftworkshop.cartMicroservice.services;

import com.gftworkshop.cartMicroservice.model.Cart;
import com.gftworkshop.cartMicroservice.model.CartProduct;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

public interface CartService {
    void addProductToCart(CartProduct cartProduct);

    void removeProductFromCart(CartProduct cartProduct);

    BigDecimal getCartTotal(Long cartId);

    void clearCart(Long cartId);

    List<Cart> identifyAbandonedCarts(Date thresholdDate);
}
