package com.gftworkshop.cartMicroservice.services;

import com.gftworkshop.cartMicroservice.model.Cart;
import com.gftworkshop.cartMicroservice.model.CartProduct;

import java.math.BigDecimal;
import java.util.List;

public interface CartService {
    void addProductToCart(Cart cart, CartProduct cartProduct);

    void removeProductFromCart(Cart cart, CartProduct cartProduct);

    BigDecimal getCartTotal(Cart cart);

    void clearCart(Cart cart);

    List<Cart> identifyAbandonedCarts();
}
