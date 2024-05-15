package com.gftworkshop.cartMicroservice.services.impl;

import com.gftworkshop.cartMicroservice.api.dto.CartDto;
import com.gftworkshop.cartMicroservice.exceptions.CartNotFoundException;
import com.gftworkshop.cartMicroservice.model.Cart;
import com.gftworkshop.cartMicroservice.model.CartProduct;
import com.gftworkshop.cartMicroservice.repositories.CartProductRepository;
import com.gftworkshop.cartMicroservice.repositories.CartRepository;
import com.gftworkshop.cartMicroservice.services.CartService;
import jakarta.transaction.Transactional;
import lombok.extern.java.Log;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class CartServiceImpl implements CartService {

    private final CartRepository cartRepository;
    private final CartProductRepository cartProductRepository;

    public CartServiceImpl(CartRepository cartRepository, CartProductRepository cartProductRepository) {
        this.cartRepository = cartRepository;
        this.cartProductRepository = cartProductRepository;
    }

    @Override
    public void addProductToCart(CartProduct cartProduct) {
        Long cartId = cartProduct.getCart().getId();
        Optional<Cart> optionalCart = cartRepository.findById(cartId);
        if (optionalCart.isPresent()) {
            Cart cart = optionalCart.get();
            cart.getCartProducts().add(cartProduct);
            cartProduct.setCart(cart);
            cartProductRepository.save(cartProduct);
            cartRepository.save(cart);
        } else {
            throw new CartNotFoundException("Cart with ID " + cartId + " not found");
        }
    }

    @Override
    public void removeProductFromCart(CartProduct cartProduct) {
        Long cartId = cartProduct.getId();
        Optional<Cart> optionalCart = cartRepository.findById(cartId);
        if (optionalCart.isPresent()) {
            Cart cart = optionalCart.get();
            cart.getCartProducts().remove(cartProduct);
            cartProduct.setCart(null);
            cartProductRepository.delete(cartProduct);
            cartRepository.save(cart);
        } else {
            throw new CartNotFoundException("Cart with ID " + cartId + " not found");
        }
    }


    @Override
    public BigDecimal getCartTotal(Long cartId) {
        Optional<Cart> optionalCart = cartRepository.findById(cartId);
        if (optionalCart.isPresent()) {
            Cart cart = optionalCart.get();
            BigDecimal total = BigDecimal.ZERO;
            for (CartProduct cartProduct : cart.getCartProducts()) {
                BigDecimal productTotal = cartProduct.getPrice().multiply(BigDecimal.valueOf(cartProduct.getQuantity()));
                total = total.add(productTotal);
            }
            return total;
        } else {
            throw new CartNotFoundException("Cart with ID " + cartId + " not found");
        }
    }


    @Transactional
    public void clearCart(Long cartId) {
        Optional<Cart> optionalCart = cartRepository.findById(cartId);
        if (optionalCart.isPresent()) {
            Cart cart = optionalCart.get();
            cartProductRepository.removeAllByCartId(cartId);
            cart.getCartProducts().clear();
            updateCartModifiedDateTime(cart);
            cartRepository.save(cart);
        } else {
            throw new CartNotFoundException("Cart with ID " + cartId + " not found");
        }
    }

    private void updateCartModifiedDateTime(Cart cart) {
        cart.setUpdated_at(new Date());
    }


    @Override
    public List<Cart> identifyAbandonedCarts(Date thresholdDate) {

        List<Cart> abandonedCarts = cartRepository.identifyAbandonedCarts(thresholdDate);

        if (abandonedCarts.isEmpty()) {
            log.info("No abandoned carts found before {}", thresholdDate);
        } else {
            log.info("Found {} abandoned carts before {}", abandonedCarts.size(), thresholdDate);
            for (Cart cart : abandonedCarts) {
                log.debug("Abandoned cart: {}, at {}", cart.getId(), cart.getUpdated_at());
            }
        }

        return abandonedCarts;

    }

    @Override
    public Cart createCart(Long userId) {
        Cart cart = new Cart();
        cart.setUpdated_at(new Date());
        cart.setUser_id(userId);
        return cartRepository.save(cart);
    }

    @Override
    public CartDto getCart(Long cartId) {
        Cart cart = cartRepository.findById(cartId).orElseThrow();
        return entityToDto(cart);
    }

    public List<Cart> getAllCarts() {
        return cartRepository.findAll();
    }

    private CartDto entityToDto(Cart cart) {
        CartDto cartDto = new CartDto();
        BeanUtils.copyProperties(cart, cartDto);
        return cartDto;
    }

}
