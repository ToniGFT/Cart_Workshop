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

    public static final String CART_NOT_FOUND = "Cart with ID ";
    public static final String NOT_FOUND = " not found";
    public static final String NOT_ENOUGH_STOCK = "Not enough stock to add product to cart. Desired amount: ";
    public static final String ACTUAL_STOCK = ". Actual stock: ";
    public static final String USER_ALREADY_HAS_CART = "User with ID ";
    public static final String ALREADY_HAS_CART = " already has a cart.";
    public static final String NO_ABANDONED_CARTS_FOUND = "No abandoned carts found before ";
    public static final String FOUND_ABANDONED_CARTS = "Found {} abandoned carts before ";
    public static final String ABANDONED_CART = "Abandoned cart: {}, at ";

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

    public void checkForAbandonedCarts() {
        LocalDate thresholdDate = LocalDate.now().minusDays(1);
        identifyAbandonedCarts(thresholdDate);
    }

    @Override
    public void addProductToCart(CartProduct cartProduct) {
        checkForAbandonedCarts();

        int actualProductAmount = productService.getProductById(cartProduct.getProductId()).getCurrent_stock();
        if (cartProduct.getQuantity()>actualProductAmount) {
            throw new CartProductInvalidQuantityException(NOT_ENOUGH_STOCK + cartProduct.getQuantity() + ACTUAL_STOCK + actualProductAmount);
        }

        Cart cart = cartRepository.findById(cartProduct.getCart().getId())
                .orElseThrow(() -> new CartNotFoundException(CART_NOT_FOUND + cartProduct.getCart().getId() + NOT_FOUND));

        cart.getCartProducts().add(cartProduct);
        cartProduct.setCart(cart);
        cartProductRepository.save(cartProduct);
        cartRepository.save(cart);
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

            totalWeight += (product.getWeight()*cartProduct.getQuantity());
            BigDecimal productTotal = cartProduct.getPrice().multiply(BigDecimal.valueOf(cartProduct.getQuantity()));
            total = total.add(productTotal);
        }

        BigDecimal weightCost = calculateWeightCost(totalWeight);
        BigDecimal tax = total.multiply(BigDecimal.valueOf(user.getCountry().getTax()/100));
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
        checkForAbandonedCarts();

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
            log.info(NO_ABANDONED_CARTS_FOUND + thresholdDate);
        } else {
            log.info(FOUND_ABANDONED_CARTS + thresholdDate, abandonedCarts.size());
            abandonedCarts.forEach(cart -> {
                log.debug(ABANDONED_CART, cart.getId(), cart.getUpdatedAt());
            });
        }

        return abandonedCarts.stream()
                .map(this::entityToDto)
                .toList();
    }

    @Override
    public CartDto createCart(Long userId) {
        checkForAbandonedCarts();

        cartRepository.findByUserId(userId).ifPresent(cart -> {
            throw new UserWithCartException(USER_ALREADY_HAS_CART + userId + ALREADY_HAS_CART);
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
        checkForAbandonedCarts();

        Cart cart = cartRepository.findById(cartId)
                .orElseThrow(() -> new CartNotFoundException(CART_NOT_FOUND + cartId + NOT_FOUND));

        for(CartProduct cartProduct:cart.getCartProducts()){
            int actualStock = productService.getProductById(cartProduct.getProductId()).getCurrent_stock();
            if(cartProduct.getQuantity()>actualStock){
                throw new CartProductInvalidQuantityException("Not enough stock. Quantity desired: "+cartProduct.getQuantity()+". Actual stock: "+actualStock);
            }
        }

        CartDto cartDto = entityToDto(cart);
        cartDto.setTotalPrice(getCartTotal(cart.getId(),cart.getUserId()));

        return cartDto;
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
