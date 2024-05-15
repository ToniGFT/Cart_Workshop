package com.gftworkshop.cartMicroservice.services.impl;

import com.gftworkshop.cartMicroservice.api.dto.Country;
import com.gftworkshop.cartMicroservice.api.dto.Product;
import com.gftworkshop.cartMicroservice.api.dto.User;
import com.gftworkshop.cartMicroservice.model.Cart;
import com.gftworkshop.cartMicroservice.model.CartProduct;
import com.gftworkshop.cartMicroservice.repositories.CartProductRepository;
import com.gftworkshop.cartMicroservice.repositories.CartRepository;
import com.gftworkshop.cartMicroservice.services.CartService;
import com.gftworkshop.cartMicroservice.services.ProductService;
import com.gftworkshop.cartMicroservice.services.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.util.*;

import static org.junit.Assert.assertThrows;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CartServiceImplTest {


    @Mock
    private CartRepository cartRepository;

    @Mock
    private CartProductRepository cartProductRepository;

    @InjectMocks
    private CartServiceImpl cartServiceImpl;
    @InjectMocks
    private ProductService productService;
    @Mock
    private UserService userService;


    private User user;
    private Cart cart;
    private Product product1;
    private Product product2;
    private CartProduct cartProduct1;
    private CartProduct cartProduct2;


    @Test
    public void testGetCartTotal_successfulCalculation() {
        when(userService.getUserById(anyLong())).thenReturn(Mono.just(new User(1L, new Country(1L, 0.07))));

        // Setup user and country
        Country country = new Country(1l,0.07);
        user = new User(1L, country);

        // Setup products
        product1 = new Product(1L, "Product1", "Description1", new BigDecimal("10.00"), 10, "Category1", 2.0);
        product2 = new Product(2L, "Product2", "Description2", new BigDecimal("20.00"), 5, "Category2", 3.0);

        // Setup cart products
        cartProduct1 = new CartProduct(1L, cart, 1L, "Product1", "Category1", "Description1", 2, new BigDecimal("10.00"));
        cartProduct2 = new CartProduct(2L, cart, 2L, "Product2", "Category2", "Description2", 3, new BigDecimal("20.00"));

        // Setup cart
        cart = new Cart(1L, 1L, null, Arrays.asList(cartProduct1, cartProduct2));

        when(userService.getUserById(anyLong())).thenReturn(Mono.just(user));
        when(cartRepository.findById(anyLong())).thenReturn(Optional.of(cart));
        when(productService.getProductById(1L)).thenReturn(Mono.just(product1));
        when(productService.getProductById(2L)).thenReturn(Mono.just(product2));



        BigDecimal expectedTotal = new BigDecimal("86.10"); // Calculation: (10*2 + 20*3) + 7% tax + 10 weight cost
        BigDecimal result = cartServiceImpl.getCartTotal(1L, 1L);

        assertEquals(0, expectedTotal.compareTo(result), "The calculated total should match expected total.");
    }



    @Test
    @DisplayName("Given a cart and a product, " +
            "when adding the product to the cart, " +
            "then the product should be added successfully")
    void addProductToCartTest() {

        Cart cart = mock(Cart.class);
        when(cart.getId()).thenReturn(1L);
        when(cart.getCartProducts()).thenReturn(new ArrayList<>());

        CartProduct cartProduct = mock(CartProduct.class);
        lenient().when(cartProduct.getId()).thenReturn(1L);

        when(cartProduct.getCart()).thenReturn(cart);

        when(cartRepository.findById(1L)).thenReturn(Optional.of(cart));
        when(cartProductRepository.save(cartProduct)).thenReturn(cartProduct);

        cartServiceImpl.addProductToCart(cartProduct);

        verify(cartProductRepository).save(cartProduct);
        verify(cartRepository).save(cart);
    }


    @Test
    @DisplayName("Given a cart with a product, " +
            "when removing the product from the cart, " +
            "then the product should be removed successfully")
    void removeProductFromCartTest() {

        CartProduct cartProduct = mock(CartProduct.class);
        Cart cart = mock(Cart.class);

        when(cartProduct.getId()).thenReturn(1L);

        when(cartRepository.findById(1L)).thenReturn(Optional.of(cart));

        cartServiceImpl.removeProductFromCart(cartProduct);

        verify(cartProductRepository).delete(cartProduct);
    }

    @Test
    @DisplayName("Given an existing cart, " +
            "when clearing the cart, " +
            "then the cart products should be cleared successfully")
    void clearCartTest() {

        Long cartId = 1L;
        Cart cart = mock(Cart.class);
        cart.setId(cartId);

        when(cartRepository.findById(cartId)).thenReturn(Optional.of(cart));

        cartServiceImpl.clearCart(cartId);

        assertTrue(cart.getCartProducts().isEmpty());
        verify(cartRepository).save(cart);
    }


    @Test
    @DisplayName("Identify Abandoned Carts - Given No Abandoned Carts " +
            "When Identifying " +
            "Then Return Empty List")
    void identifyAbandonedCarts_NoAbandonedCartsTest() {

        Date thresholdDate = new Date(System.currentTimeMillis() - 86400000);

        when(cartRepository.identifyAbandonedCarts(thresholdDate)).thenReturn(new ArrayList<>());

        List<Cart> result = cartServiceImpl.identifyAbandonedCarts(thresholdDate);

        assertEquals(0, result.size());

        verify(cartRepository).identifyAbandonedCarts(thresholdDate);
    }


    @Test
    @DisplayName("Identify Abandoned Carts - Given Abandoned Carts " +
            "When Identifying " +
            "Then Return List of Abandoned Carts")
    void identifyAbandonedCarts_AbandonedCartsExistTest() {

        Date thresholdDate = new Date(System.currentTimeMillis() - 86400000);

        Cart cart1 = mock(Cart.class);
        Cart cart2 = mock(Cart.class);

        List<Cart> abandonedCarts = Arrays.asList(cart1, cart2);

        when(cartRepository.identifyAbandonedCarts(thresholdDate)).thenReturn(abandonedCarts);

        List<Cart> result = cartServiceImpl.identifyAbandonedCarts(thresholdDate);

        assertEquals(abandonedCarts.size(), result.size());
        assertTrue(result.contains(cart1));
        assertTrue(result.contains(cart2));

        verify(cartRepository).identifyAbandonedCarts(thresholdDate);
    }

    @Test
    @DisplayName("Given a user id, " +
            "when creating a cart, " +
            "then a cart should be created successfully")
    void createCartTest() {
        Long userId = 123L;

        Cart cart = mock(Cart.class);
        when(cart.getId()).thenReturn(userId);
        when(cart.getUser_id()).thenReturn(userId);

        when(cartRepository.save(any(Cart.class))).thenReturn(cart);

        Cart createdCart = cartServiceImpl.createCart(userId);

        assertEquals(userId, createdCart.getUser_id());
        assertEquals(userId, createdCart.getId());
        verify(cartRepository).save(any(Cart.class));
    }

    @Test
    @DisplayName("Given a cartId, " +
            "when getting the cart, " +
            "then return the corresponding cart")
    void getCartTest() {
        Long cartId = 123L;

        Cart cart = mock(Cart.class);
        when(cartRepository.findById(cartId)).thenReturn(Optional.of(cart));

        Cart retrievedCart = cartServiceImpl.getCart(cartId);

        assertEquals(cart, retrievedCart);
        verify(cartRepository).findById(cartId);
    }

    @Test
    @DisplayName("Given existing carts, " +
            "when retrieving all carts, " +
            "then return the list of all carts")
    void getAllCartsTest() {
        Cart cart1 = mock(Cart.class);
        Cart cart2 = mock(Cart.class);
        List<Cart> expectedCarts = Arrays.asList(cart1, cart2);

        when(cartRepository.findAll()).thenReturn(expectedCarts);

        List<Cart> actualCarts = cartServiceImpl.getAllCarts();

        assertEquals(expectedCarts.size(), actualCarts.size());
        assertTrue(actualCarts.contains(cart1));
        assertTrue(actualCarts.contains(cart2));
        verify(cartRepository).findAll();
    }

}
