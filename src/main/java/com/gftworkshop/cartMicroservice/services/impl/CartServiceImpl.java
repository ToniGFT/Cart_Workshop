package com.gftworkshop.cartMicroservice.services.impl;

import com.gftworkshop.cartMicroservice.model.Cart;
import com.gftworkshop.cartMicroservice.model.CartProduct;
import com.gftworkshop.cartMicroservice.repositories.CartProductRepository;
import com.gftworkshop.cartMicroservice.repositories.CartRepository;
import com.gftworkshop.cartMicroservice.services.CartService;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

@Service
public class CartServiceImpl implements CartService {

    private final CartRepository cartRepository;
    private final CartProductRepository cartProductRepository;

    public CartServiceImpl(CartRepository cartRepository, CartProductRepository cartProductRepository) {
        this.cartRepository = cartRepository;
        this.cartProductRepository = cartProductRepository;
    }

    @Override
    public void addProductToCart(Cart cart, CartProduct cartProduct) {
        cart.getCartProducts().add(cartProduct);
        cartProduct.setCart(cart);
        cartProductRepository.save(cartProduct);
        cartRepository.save(cart);
    }

    @Override
    public void removeProductFromCart(Cart cart, CartProduct cartProduct) {
        cart.getCartProducts().remove(cartProduct);
        cartProduct.setCart(null);
        cartProductRepository.delete(cartProduct);
        updateCartModifiedDateTime(cart);
    }

    @Override
    public BigDecimal getCartTotal(Cart cart) {
        BigDecimal total = BigDecimal.ZERO;
        for (CartProduct cartProduct : cart.getCartProducts()) {
            BigDecimal productTotal = cartProduct.getPrice().multiply(BigDecimal.valueOf(cartProduct.getQuantity()));
            total = total.add(productTotal);
        }
        return total;
    }

    @Override
    public void clearCart(Cart cart) {
        cart.getCartProducts().clear();
        cartRepository.save(cart);
        updateCartModifiedDateTime(cart);
    }

    private void updateCartModifiedDateTime(Cart cart) {
        cart.setUpdated_at(new Date());
    }

    @Override
    public List<Cart> identifyAbandonedCarts() {

        Date currentDate = new Date();

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(currentDate);
        calendar.add(Calendar.DAY_OF_MONTH, -1);
        Date thresholdDate = calendar.getTime();

        return cartRepository.identifyAbandonedCarts(thresholdDate);
    }


}
