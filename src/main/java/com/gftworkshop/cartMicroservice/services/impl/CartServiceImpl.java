package com.gftworkshop.cartMicroservice.services.impl;

import com.gftworkshop.cartMicroservice.api.dto.CartDto;
import com.gftworkshop.cartMicroservice.api.dto.Product;
import com.gftworkshop.cartMicroservice.api.dto.User;
import com.gftworkshop.cartMicroservice.exceptions.CartNotFoundException;
import com.gftworkshop.cartMicroservice.exceptions.CartProductInvalidQuantityException;
import com.gftworkshop.cartMicroservice.exceptions.UserWithCartException;
import com.gftworkshop.cartMicroservice.model.Cart;
import com.gftworkshop.cartMicroservice.model.CartProduct;
import com.gftworkshop.cartMicroservice.repositories.CartProductRepository;
import com.gftworkshop.cartMicroservice.repositories.CartRepository;
import com.gftworkshop.cartMicroservice.services.CartService;
import com.gftworkshop.cartMicroservice.services.ProductService;
import com.gftworkshop.cartMicroservice.services.UserService;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Service
@Slf4j
public class CartServiceImpl implements CartService {

    private static final String CART_NOT_FOUND = "Cart with ID ";
    private static final String NOT_FOUND = " not found";

    private final CartRepository cartRepository;
    private final CartProductRepository cartProductRepository;
    private final ProductService productService;
    private final UserService userService;

    public CartServiceImpl(CartRepository cartRepository, CartProductRepository cartProductRepository, ProductService productService, UserService userService) {
        this.cartRepository = cartRepository;
        this.cartProductRepository = cartProductRepository;
        this.productService = productService;
        this.userService = userService;
    }

    @Override
    public void addProductToCart(CartProduct cartProduct) {
        int actualProductAmount = productService.getProductById(cartProduct.getProductId()).getStock();

        if(actualProductAmount>=cartProduct.getQuantity()){
            Cart cart = cartRepository.findById(cartProduct.getCart().getId())
                    .orElseThrow(() -> new CartNotFoundException("Cart with ID " + cartProduct.getCart().getId() + " not found"));

            cart.getCartProducts().add(cartProduct);
            cartProduct.setCart(cart);
            cartProductRepository.save(cartProduct);
            cartRepository.save(cart);
        } else {
            throw new CartProductInvalidQuantityException("Not enough stock to add product to cart. Desired amount: "+cartProduct.getQuantity()+". Actual stock: "+actualProductAmount);
        }
    }

    @Override
    public BigDecimal getCartTotal(Long cartId, Long userId) {
        User user = userService.getUserById(userId);

        Cart cart = cartRepository.findById(cartId)
                .orElseThrow(() -> new CartNotFoundException(CART_NOT_FOUND + cartId + NOT_FOUND));

        BigDecimal total = BigDecimal.ZERO;
        double totalWeight = 0.0;

        for (CartProduct cartProduct : cart.getCartProducts()) {
            Product product = productService.getProductById(cartProduct.getProductId());

            totalWeight += product.getWeight();
            BigDecimal productTotal = cartProduct.getPrice().multiply(BigDecimal.valueOf(cartProduct.getQuantity()));
            total = total.add(productTotal);
        }

        BigDecimal weightCost = calculateWeightCost(totalWeight);
        BigDecimal tax = total.multiply(BigDecimal.valueOf(user.getCountry().getTax()));
        total = total.add(tax).add(weightCost);

        return total;
    }

    BigDecimal calculateWeightCost(double totalWeight) {
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
        Cart cart = cartRepository.findById(cartId)
                .orElseThrow(() -> new CartNotFoundException(CART_NOT_FOUND + cartId + NOT_FOUND));

        cartProductRepository.removeAllByCartId(cartId);
        cart.getCartProducts().clear();
        updateCartModifiedDateTime(cart);
        cartRepository.save(cart);
    }

    private void updateCartModifiedDateTime(Cart cart) {
        cart.setUpdatedAt(LocalDate.now());
    }

    @Override
    public List<CartDto> identifyAbandonedCarts(LocalDate thresholdDate) {
        List<Cart> abandonedCarts = cartRepository.identifyAbandonedCarts(thresholdDate);

        if (abandonedCarts.isEmpty()) {
            log.info("No abandoned carts found before {}", thresholdDate);
        } else {
            log.info("Found {} abandoned carts before {}", abandonedCarts.size(), thresholdDate);
            abandonedCarts.forEach(cart -> log.debug("Abandoned cart: {}, at {}", cart.getId(), cart.getUpdatedAt()));
        }

        return abandonedCarts.stream()
                .map(this::entityToDto)
                .toList();
    }

    @Override
    public CartDto createCart(Long userId) {
        cartRepository.findByUserId(userId).ifPresent(cart -> {
            throw new UserWithCartException("User with ID " + userId + " already has a cart.");
        });

        Cart cart = Cart.builder()
                .updatedAt(LocalDate.now())
                .userId(userId)
                .build();
        cart = cartRepository.save(cart);
        return entityToDto(cart);
    }

    @Override
    public CartDto getCart(Long cartId) {
        Cart cart = cartRepository.findById(cartId)
                .orElseThrow(() -> new CartNotFoundException(CART_NOT_FOUND + cartId + NOT_FOUND));
        return entityToDto(cart);
    }

    public List<Cart> getAllCarts() {
        return cartRepository.findAll();
    }

    private CartDto entityToDto(Cart cart) {
        CartDto cartDto = CartDto.builder().build();
        BeanUtils.copyProperties(cart, cartDto);
        return cartDto;
    }
}
