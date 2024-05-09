package com.gftworkshop.cartMicroservice.services.impl;

import com.gftworkshop.cartMicroservice.model.Cart;
import com.gftworkshop.cartMicroservice.model.CartProduct;
import com.gftworkshop.cartMicroservice.repositories.CartProductRepository;
import com.gftworkshop.cartMicroservice.repositories.CartRepository;
import com.gftworkshop.cartMicroservice.services.CartService;
import com.gftworkshop.cartMicroservice.services.impl.CartServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

public class CartServiceImplTest {

    @Mock
    private CartRepository cartRepository;

    @Mock
    private CartProductRepository cartProductRepository;

    @InjectMocks
    private CartServiceImpl cartService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    @DisplayName("Given a cart and a product, " +
            "when adding the product to the cart, " +
            "then the product should be added successfully")
    void addProductToCartTest() {
        Cart cart = mock(Cart.class);
        CartProduct cartProduct = mock(CartProduct.class);

        when(cartProductRepository.save(cartProduct)).thenReturn(cartProduct);

        cartService.addProductToCart(cart, cartProduct);

        verify(cartProductRepository).save(cartProduct);
        verify(cartRepository).save(cart);
    }

    @Test
    @DisplayName("Given a cart with a product, " +
            "when removing the product from the cart, " +
            "then the product should be removed successfully")
    void removeProductFromCartTest() {
        Cart cart = mock(Cart.class);
        CartProduct cartProduct = mock(CartProduct.class);

        when(cartProductRepository.save(cartProduct)).thenReturn(cartProduct);

        cartService.addProductToCart(cart, cartProduct);
        cartService.removeProductFromCart(cart, cartProduct);

        verify(cartProductRepository).delete(cartProduct);
        verify(cartRepository).save(cart);
    }

    @Test
    @DisplayName("Given a cart with multiple products, " +
            "when calculating the cart total, " +
            "then the total should be calculated correctly")
    void getCartTotalTest() {
        Cart cart = mock(Cart.class);
        CartProduct cartProduct1 = mock(CartProduct.class);
        when(cartProduct1.getPrice()).thenReturn(new BigDecimal("10"));
        when(cartProduct1.getQuantity()).thenReturn(2);

        CartProduct cartProduct2 = mock(CartProduct.class);
        when(cartProduct2.getPrice()).thenReturn(new BigDecimal("15"));
        when(cartProduct2.getQuantity()).thenReturn(3);

        List<CartProduct> cartProducts = new ArrayList<>();
        cartProducts.add(cartProduct1);
        cartProducts.add(cartProduct2);
        when(cart.getCartProducts()).thenReturn(cartProducts);

        BigDecimal product1Total = cartProduct1.getPrice().multiply(BigDecimal.valueOf(cartProduct1.getQuantity()));
        BigDecimal product2Total = cartProduct2.getPrice().multiply(BigDecimal.valueOf(cartProduct2.getQuantity()));

        BigDecimal expectedTotal = new BigDecimal("65");
        BigDecimal actualTotal = cartService.getCartTotal(cart);

        assertEquals(expectedTotal, actualTotal);
    }

    @Test
    @DisplayName("Given a cart with products, " +
            "when clearing the cart, " +
            "then the cart should be cleared successfully")
    void clearCartTest() {
        Cart cart = mock(Cart.class);
        CartProduct cartProduct1 = mock(CartProduct.class);
        CartProduct cartProduct2 = mock(CartProduct.class);

        List<CartProduct> cartProducts = new ArrayList<>();
        cartProducts.add(cartProduct1);
        cartProducts.add(cartProduct2);
        when(cart.getCartProducts()).thenReturn(cartProducts);

        cartService.clearCart(cart);

        assertEquals(0, cart.getCartProducts().size());
        verify(cartRepository).save(cart);
    }


    @Test
    public void testIdentifyAbandonedCarts() {
        Date currentDate = new Date();

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(currentDate);
        calendar.add(Calendar.DAY_OF_MONTH, -1);
        Date thresholdDate = calendar.getTime();

        CartRepository cartRepository = mock(CartRepository.class);

        Cart cart = new Cart();
        cart.setId(1L);
        cart.setUpdated_at(currentDate);

        List<Cart> abandonedCarts = new ArrayList<>();
        abandonedCarts.add(cart);

        when(cartRepository.identifyAbandonedCarts(thresholdDate)).thenReturn(abandonedCarts);

        CartService cartService = mock(CartService.class);

        when(cartService.identifyAbandonedCarts()).thenReturn(abandonedCarts);

        List<Cart> result = cartService.identifyAbandonedCarts();

        verify(cartService, times(1)).identifyAbandonedCarts();

        assertEquals(abandonedCarts, result);
    }


}
