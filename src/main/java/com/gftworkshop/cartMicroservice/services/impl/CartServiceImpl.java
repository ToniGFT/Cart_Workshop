package com.gftworkshop.cartMicroservice.services.impl;

import com.gftworkshop.cartMicroservice.api.dto.Product;
import com.gftworkshop.cartMicroservice.api.dto.User;
import com.gftworkshop.cartMicroservice.exceptions.CartNotFoundException;
import com.gftworkshop.cartMicroservice.model.Cart;
import com.gftworkshop.cartMicroservice.model.CartProduct;
import com.gftworkshop.cartMicroservice.repositories.CartProductRepository;
import com.gftworkshop.cartMicroservice.repositories.CartRepository;
import com.gftworkshop.cartMicroservice.services.CartService;
import com.gftworkshop.cartMicroservice.services.ProductService;
import com.gftworkshop.cartMicroservice.services.UserService;
import jakarta.transaction.Transactional;
import lombok.extern.java.Log;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class CartServiceImpl implements CartService {

    private final UserService userService;
    private final ProductService productService;
    private final CartRepository cartRepository;
    private final CartProductRepository cartProductRepository;

    public CartServiceImpl(UserService userService, ProductService productService, CartRepository cartRepository, CartProductRepository cartProductRepository) {
        this.userService = userService;
        this.productService = productService;
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
    public BigDecimal getCartTotal(Long cartId, Long userId) {
        User user = userService.getUserById(userId).block();

        Optional<Cart> optionalCart = cartRepository.findById(cartId);
        if (!optionalCart.isPresent()) {
            throw new CartNotFoundException("Cart with ID " + cartId + " not found");
        }

        Cart cart = optionalCart.get();
        BigDecimal total = BigDecimal.ZERO;
        double totalWeight = 0.0;

        for (CartProduct cartProduct : cart.getCartProducts()) {
            Product product = productService.getProductById(cartProduct.getProductId()).block();
            totalWeight += product.getWeight();
            BigDecimal productTotal = cartProduct.getPrice().multiply(BigDecimal.valueOf(cartProduct.getQuantity()));
            total = total.add(productTotal);
        }

        BigDecimal weightCost = calculateWeightCost(totalWeight);
        BigDecimal tax = total.multiply(new BigDecimal(user.getCountry().getTax()));
        total = total.add(tax).add(weightCost);

        return total;
    }

    private BigDecimal calculateWeightCost(double totalWeight) {
        if (totalWeight > 20) {
            return new BigDecimal("50");
        } else if (totalWeight > 10) {
            return new BigDecimal("20");
        } else if (totalWeight > 5) {
            return new BigDecimal("10");
        }
        return new BigDecimal("5");
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
    public Cart getCart(Long cartId) {
        return cartRepository.findById(cartId).orElseThrow();
    }

    public List<Cart> getAllCarts() {
        return cartRepository.findAll();
    }

}
