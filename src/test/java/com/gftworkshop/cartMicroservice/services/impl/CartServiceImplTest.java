package com.gftworkshop.cartMicroservice.services.impl;

import com.gftworkshop.cartMicroservice.api.dto.CartDto;
import com.gftworkshop.cartMicroservice.api.dto.Country;
import com.gftworkshop.cartMicroservice.api.dto.Product;
import com.gftworkshop.cartMicroservice.api.dto.User;
import com.gftworkshop.cartMicroservice.exceptions.CartNotFoundException;
import com.gftworkshop.cartMicroservice.exceptions.CartProductNotFoundException;
import com.gftworkshop.cartMicroservice.exceptions.UserNotFoundException;
import com.gftworkshop.cartMicroservice.exceptions.UserWithCartException;
import com.gftworkshop.cartMicroservice.model.Cart;
import com.gftworkshop.cartMicroservice.model.CartProduct;
import com.gftworkshop.cartMicroservice.repositories.CartProductRepository;
import com.gftworkshop.cartMicroservice.repositories.CartRepository;
import com.gftworkshop.cartMicroservice.services.ProductService;
import com.gftworkshop.cartMicroservice.services.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CartServiceImplTest {

    private CartRepository cartRepository;
    private CartProductRepository cartProductRepository;
    private CartServiceImpl cartServiceImpl;
    private UserService userService;
    private ProductService productService;

    @BeforeEach
    void setUp() {
        cartRepository = mock(CartRepository.class);
        cartProductRepository = mock(CartProductRepository.class);
        userService = mock(UserService.class);
        productService = mock(ProductService.class);
        cartServiceImpl = new CartServiceImpl(cartRepository, cartProductRepository, productService, userService);
    }

    @Test
    @DisplayName("Given a cart and a product, " +
            "when adding the product to the cart, " +
            "then the product should be added successfully")
    void addProductToCartTest() {
        Cart cart = Cart.builder().id(1L).cartProducts(new ArrayList<>()).build();

        CartProduct cartProduct = CartProduct.builder().id(1L).cart(cart).build();

        when(cartRepository.findById(1L)).thenReturn(Optional.of(cart));
        when(cartProductRepository.save(cartProduct)).thenReturn(cartProduct);

        cartServiceImpl.addProductToCart(cartProduct);

        verify(cartProductRepository).save(cartProduct);
        verify(cartRepository).save(cart);
    }

    @Test
    @DisplayName("Given a cart with multiple products and a user, " +
            "when calculating the cart total including tax and weight costs, " +
            "then the total should be calculated correctly")
    void getCartTotalTest() {
        User user = User.builder().country(new Country(1L, 0.07)).build();

        Product product1 = Product.builder().weight(2.0).build();
        Product product2 = Product.builder().weight(3.5).build();

        Cart cart = Cart.builder().build();

        CartProduct cartProduct1 = CartProduct.builder()
                .productId(1L)
                .price(new BigDecimal("10"))
                .quantity(2)
                .cart(cart)
                .build();

        CartProduct cartProduct2 = CartProduct.builder()
                .productId(2L)
                .price(new BigDecimal("15"))
                .quantity(3)
                .cart(cart)
                .build();

        List<CartProduct> cartProducts = Arrays.asList(cartProduct1, cartProduct2);
        cart.setCartProducts(cartProducts);

        when(userService.getUserById(1L)).thenReturn(user);
        when(productService.getProductById(1L)).thenReturn(product1);
        when(productService.getProductById(2L)).thenReturn(product2);

        when(cartRepository.findById(1L)).thenReturn(Optional.of(cart));

        BigDecimal expectedTotal = new BigDecimal("65");
        BigDecimal weightCost = new BigDecimal("10");
        BigDecimal tax = expectedTotal.multiply(new BigDecimal("0.07"));
        expectedTotal = expectedTotal.add(tax).add(weightCost);

        BigDecimal actualTotal = cartServiceImpl.getCartTotal(1L, 1L);
        actualTotal = actualTotal.setScale(2, RoundingMode.HALF_UP);
        assertEquals(expectedTotal, actualTotal);

        verify(userService).getUserById(1L);
        verify(productService, times(2)).getProductById(anyLong());
        verify(cartRepository).findById(1L);
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
        LocalDate thresholdDate = LocalDate.now().minusDays(1);

        when(cartRepository.identifyAbandonedCarts(thresholdDate)).thenReturn(new ArrayList<>());

        List<CartDto> result = cartServiceImpl.identifyAbandonedCarts(thresholdDate);

        assertEquals(0, result.size());

        verify(cartRepository).identifyAbandonedCarts(thresholdDate);
    }

    @Test
    @DisplayName("Identify Abandoned Carts - Given Abandoned Carts " +
            "When Identifying " +
            "Then Return List of Abandoned Carts")
    void identifyAbandonedCarts_AbandonedCartsExistTest() {
        LocalDate thresholdDate = LocalDate.now().minusDays(1);

        when(cartRepository.identifyAbandonedCarts(thresholdDate)).thenReturn(Arrays.asList(Cart.builder().build(), Cart.builder().build()));

        List<CartDto> result = cartServiceImpl.identifyAbandonedCarts(thresholdDate);

        assertEquals(2, result.size());
        verify(cartRepository).identifyAbandonedCarts(thresholdDate);
    }


    @Test
    @DisplayName("Given a user id, " +
            "when creating a cart, " +
            "then a cart should be created successfully")
    void createCartTest() {
        Long userId = 123L;

        Cart cart = Cart.builder().id(userId).user_id(userId).build();

        when(cartRepository.save(any(Cart.class))).thenReturn(cart);

        CartDto createdCart = cartServiceImpl.createCart(userId);

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

        Cart cart = Cart.builder().build();
        when(cartRepository.findById(cartId)).thenReturn(Optional.of(cart));

        CartDto retrievedCartDto = cartServiceImpl.getCart(cartId);

        assertEquals(cart.getId(), retrievedCartDto.getId());
        verify(cartRepository).findById(cartId);
    }

    @Test
    @DisplayName("Given existing carts, " +
            "when retrieving all carts, " +
            "then return the list of all carts")
    void getAllCartsTest() {
        Cart cart1 = Cart.builder().build();
        Cart cart2 = Cart.builder().build();
        List<Cart> expectedCarts = Arrays.asList(cart1, cart2);

        when(cartRepository.findAll()).thenReturn(expectedCarts);

        List<Cart> actualCarts = cartServiceImpl.getAllCarts();

        assertEquals(expectedCarts.size(), actualCarts.size());
        assertTrue(actualCarts.contains(cart1));
        assertTrue(actualCarts.contains(cart2));
        verify(cartRepository).findAll();
    }


    @Test
    @DisplayName("Given a non-existent cart, " +
            "when adding a product to the cart, " +
            "then a CartNotFoundException should be thrown")
    void addProductToCart_CartNotFoundExceptionTest() {

        CartProduct cartProduct = mock(CartProduct.class);
        when(cartProduct.getCart()).thenReturn(mock(Cart.class));
        when(cartProduct.getCart().getId()).thenReturn(1L);

        when(cartRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(CartNotFoundException.class, () -> {
            cartServiceImpl.addProductToCart(cartProduct);
        });
    }

    @Test
    @DisplayName("Given a non-existent cart, " +
            "when clearing the cart, " +
            "then a CartNotFoundException should be thrown")
    void clearCart_CartNotFoundExceptionTest() {

        when(cartRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(CartNotFoundException.class, () -> {
            cartServiceImpl.clearCart(1L);
        });
    }

    @Test
    @DisplayName("Given a non-existent user, " +
            "when calculating the cart total, " +
            "then a UserNotFoundException should be thrown")
    void getCartTotal_UserNotFoundExceptionTest() {
        Long cartId = 1L;
        Long userId = 1L;

        when(userService.getUserById(userId)).thenThrow(new UserNotFoundException("User with ID " + userId + " not found"));

        assertThrows(UserNotFoundException.class, () -> {
            cartServiceImpl.getCartTotal(cartId, userId);
        });
    }


    @Test
    @DisplayName("Given a non-existent product in the cart, " +
            "when calculating the cart total, " +
            "then a CartProductNotFoundException should be thrown")
    void getCartTotal_CartProductNotFoundExceptionTest() {
        Long cartId = 1L;
        Long userId = 1L;

        CartProduct cartProduct = CartProduct.builder().productId(999L).build();
        Cart cart = Cart.builder().cartProducts(Collections.singletonList(cartProduct)).build();

        when(cartRepository.findById(cartId)).thenReturn(Optional.of(cart));
        when(userService.getUserById(userId)).thenReturn(User.builder().build());
        when(productService.getProductById(cartProduct.getProductId())).thenThrow(new CartProductNotFoundException("Product with ID " + cartProduct.getProductId() + " not found"));

        assertThrows(CartProductNotFoundException.class, () -> {
            cartServiceImpl.getCartTotal(cartId, userId);
        });
    }

    @Test
    @DisplayName("Given a non-existent cart, " +
            "when calculating the cart total, " +
            "then a CartNotFoundException should be thrown")
    void getCartTotal_CartNotFoundExceptionTest() {
        Long cartId = 1L;
        Long userId = 1L;

        when(userService.getUserById(userId)).thenReturn(User.builder().build());
        when(cartRepository.findById(cartId)).thenReturn(Optional.empty());

        assertThrows(CartNotFoundException.class, () -> {
            cartServiceImpl.getCartTotal(cartId, userId);
        });
    }

    @Test
    @DisplayName("Given a user with an existing cart, " +
            "when creating a new cart, " +
            "then a UserWithCartException should be thrown")
    void createCart_UserWithCartExceptionTest() {
        Long userId = 123L;

        Cart existingCart = Cart.builder().user_id(userId).build();

        when(cartRepository.findByUserId(userId)).thenReturn(Optional.of(existingCart));

        UserWithCartException exception = assertThrows(UserWithCartException.class, () -> {
            cartServiceImpl.createCart(userId);
        });

        assertEquals("User with ID " + userId + " already has a cart.", exception.getMessage());
    }


}
