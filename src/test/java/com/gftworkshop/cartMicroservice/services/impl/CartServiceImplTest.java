package com.gftworkshop.cartMicroservice.services.impl;

import com.gftworkshop.cartMicroservice.api.dto.Country;
import com.gftworkshop.cartMicroservice.api.dto.Product;
import com.gftworkshop.cartMicroservice.api.dto.User;
import com.gftworkshop.cartMicroservice.model.Cart;
import com.gftworkshop.cartMicroservice.model.CartProduct;
import com.gftworkshop.cartMicroservice.repositories.CartProductRepository;
import com.gftworkshop.cartMicroservice.repositories.CartRepository;
import com.gftworkshop.cartMicroservice.services.ProductService;
import com.gftworkshop.cartMicroservice.services.UserService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.util.*;

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
    @InjectMocks
    private UserService userService;

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
    @DisplayName("Given a cart with multiple products, " +
            "when calculating the cart total, " +
            "then the total should be calculated correctly including tax and weight cost")
    void getCartTotalTest() {
        Long cartId = 1L;
        Long userId = 1L;

        // Mocking dependencies
        Cart cart = mock(Cart.class);
        User user = mock(User.class);
        Country country = mock(Country.class);
        Product product1 = mock(Product.class);
        Product product2 = mock(Product.class);

        // Setup user and country for tax calculation
        when(user.getCountry()).thenReturn(country);
        when(country.getTax()).thenReturn(0.10); // 10% tax

        // Setup product weights
        when(product1.getWeight()).thenReturn(5.0); // 5 kg
        when(product2.getWeight()).thenReturn(10.0); // 10 kg

        // Mock CartProducts within the cart
        CartProduct cartProduct1 = mock(CartProduct.class);
        when(cartProduct1.getPrice()).thenReturn(new BigDecimal("10"));
        when(cartProduct1.getQuantity()).thenReturn(2);
        when(cartProduct1.getProductId()).thenReturn(101L);
        when(productService.getProductById(101L)).thenReturn(Mono.just(product1));

        CartProduct cartProduct2 = mock(CartProduct.class);
        when(cartProduct2.getPrice()).thenReturn(new BigDecimal("15"));
        when(cartProduct2.getQuantity()).thenReturn(3);
        when(cartProduct2.getProductId()).thenReturn(102L);
        when(productService.getProductById(102L)).thenReturn(Mono.just(product2));

        List<CartProduct> cartProducts = Arrays.asList(cartProduct1, cartProduct2);
        when(cart.getCartProducts()).thenReturn(cartProducts);

        // Setup repository and service calls
        when(cartRepository.findById(cartId)).thenReturn(Optional.of(cart));
        when(userService.getUserById(userId)).thenReturn(Mono.just(user));

        // Expected values calculation
        BigDecimal expectedTotal = new BigDecimal("65"); // Base total from product prices and quantities
        BigDecimal weightCost = new BigDecimal("10"); // From weight 15kg (>5kg and <=10kg)
        BigDecimal tax = expectedTotal.multiply(new BigDecimal("0.10")); // 10% tax
        expectedTotal = expectedTotal.add(tax).add(weightCost);

        // Testing getCartTotal
        BigDecimal actualTotal = cartServiceImpl.getCartTotal(cartId, userId);

        assertEquals(expectedTotal, actualTotal);
        verify(productService, times(1)).getProductById(101L);
        verify(productService, times(1)).getProductById(102L);
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
