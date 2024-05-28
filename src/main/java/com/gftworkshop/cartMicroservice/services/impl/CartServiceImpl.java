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
        verifyProductStock(cartProduct);

        Cart cart = getCartById(cartProduct.getCart().getId());
        addToCart(cart, cartProduct);
    }


    public void addToCart(Cart cart, CartProduct cartProduct) {
        cart.getCartProducts().add(cartProduct);
        cartProduct.setCart(cart);
        cartProductRepository.save(cartProduct);
    }


    public void verifyProductStock(CartProduct cartProduct) {
        int actualProductAmount = productService.getProductById(cartProduct.getProductId()).getCurrentStock();

        if (cartProduct.getQuantity() > actualProductAmount) {
            throw new CartProductInvalidQuantityException(NOT_ENOUGH_STOCK + cartProduct.getQuantity() + ACTUAL_STOCK + actualProductAmount);
        }
    }


    @Override
    public BigDecimal getCartTotal(Long cartId, Long userId) {
        User user = getUserById(userId);
        Cart cart = getCartById(cartId);

        BigDecimal total = calculateProductsTotal(cart);
        BigDecimal tax = calculateTax(total, user);
        BigDecimal weightCost = calculateWeightCost(calculateTotalWeight(cart));

        return total.add(tax).add(weightCost);
    }

    public User getUserById(Long userId) {
        return userService.getUserById(userId);
    }


    public BigDecimal calculateProductsTotal(Cart cart) {
        BigDecimal total = BigDecimal.ZERO;
        for (CartProduct cartProduct : cart.getCartProducts()) {
            BigDecimal productTotal = cartProduct.getPrice().multiply(BigDecimal.valueOf(cartProduct.getQuantity()));
            total = total.add(productTotal);
        }
        return total;
    }

    public BigDecimal calculateTax(BigDecimal total, User user) {
        return total.multiply(BigDecimal.valueOf(user.getCountry().getTax() / 100));
    }

    public BigDecimal calculateWeightCost(double totalWeight) {
        if (totalWeight > 20) {
            return new BigDecimal("50");
        } else if (totalWeight > 10) {
            return new BigDecimal("20");
        } else if (totalWeight > 5) {
            return new BigDecimal("10");
        }
        return new BigDecimal("5");
    }

    public double calculateTotalWeight(Cart cart) {
        double totalWeight = 0.0;
        for (CartProduct cartProduct : cart.getCartProducts()) {
            Product product = productService.getProductById(cartProduct.getProductId());
            totalWeight += (product.getWeight() * cartProduct.getQuantity());
        }
        return totalWeight;
    }


    @Transactional
    public void clearCart(Long cartId) {
        checkForAbandonedCarts();

        Cart cart = getCartById(cartId);
        cartProductRepository.removeAllByCartId(cartId);
        cart.getCartProducts().clear();
        updateCartModifiedDateTime(cart);
        cartRepository.save(cart);
    }

    public void updateCartModifiedDateTime(Cart cart) {
        cart.setUpdatedAt(LocalDate.now());
    }

    @Override
    public List<CartDto> identifyAbandonedCarts(LocalDate thresholdDate) {
        List<Cart> abandonedCarts = findAbandonedCarts(thresholdDate);
        logAbandonedCarts(abandonedCarts, thresholdDate);
        return mapCartsToDto(abandonedCarts);
    }

    public List<Cart> findAbandonedCarts(LocalDate thresholdDate) {
        return cartRepository.identifyAbandonedCarts(thresholdDate);
    }

    public void logAbandonedCarts(List<Cart> abandonedCarts, LocalDate thresholdDate) {
        if (abandonedCarts.isEmpty()) {
            log.info(NO_ABANDONED_CARTS_FOUND + "{}", thresholdDate);
        } else {
            log.info(FOUND_ABANDONED_CARTS + "{}", thresholdDate, abandonedCarts.size());
            abandonedCarts.forEach(cart ->
                    log.debug(ABANDONED_CART, cart.getId(), cart.getUpdatedAt())
            );
        }
    }

    public List<CartDto> mapCartsToDto(List<Cart> abandonedCarts) {
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
        Cart cart = getCartById(cartId);
        verifyCartProductsStock(cart);
        return createCartDto(cart);
    }

    public Cart getCartById(Long cartId) {
        return cartRepository.findById(cartId)
                .orElseThrow(() -> new CartNotFoundException(CART_NOT_FOUND + cartId + NOT_FOUND));
    }

    public CartDto createCartDto(Cart cart) {
        CartDto cartDto = entityToDto(cart);
        cartDto.setTotalPrice(getCartTotal(cart.getId(), cart.getUserId()));
        return cartDto;
    }

    public void verifyCartProductsStock(Cart cart) {
        for (CartProduct cartProduct : cart.getCartProducts()) {
            int actualStock = productService.getProductById(cartProduct.getProductId()).getCurrentStock();
            if (cartProduct.getQuantity() > actualStock) {
                throw new CartProductInvalidQuantityException("Not enough stock. Quantity desired: " + cartProduct.getQuantity() + ACTUAL_STOCK + actualStock);
            }
        }
    }

    public List<Cart> getAllCarts() {
        return cartRepository.findAll();
    }

    public CartDto entityToDto(Cart cart) {
        CartDto cartDto = CartDto.builder().build();
        BeanUtils.copyProperties(cart, cartDto);
        return cartDto;
    }
}
