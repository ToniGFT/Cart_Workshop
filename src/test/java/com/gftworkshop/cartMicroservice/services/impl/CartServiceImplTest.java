package com.gftworkshop.cartMicroservice.services.impl;

import com.gftworkshop.cartMicroservice.api.dto.CartDto;
import com.gftworkshop.cartMicroservice.api.dto.Country;
import com.gftworkshop.cartMicroservice.api.dto.Product;
import com.gftworkshop.cartMicroservice.api.dto.User;
import com.gftworkshop.cartMicroservice.exceptions.*;
import com.gftworkshop.cartMicroservice.model.Cart;
import com.gftworkshop.cartMicroservice.model.CartProduct;
import com.gftworkshop.cartMicroservice.repositories.CartProductRepository;
import com.gftworkshop.cartMicroservice.repositories.CartRepository;
import com.gftworkshop.cartMicroservice.services.ProductService;
import com.gftworkshop.cartMicroservice.services.UserService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;


@Slf4j
@ExtendWith(MockitoExtension.class)
class CartServiceImplTest {

    @Mock
    private CartRepository cartRepository;

    @Mock
    private CartProductRepository cartProductRepository;

    @Mock
    private UserService userService;

    @Mock
    private ProductService productService;

    @InjectMocks
    private CartServiceImpl cartServiceImpl;

    @Test
    @DisplayName("Given a cart and a product, when adding the product to the cart, if there isn't enough stock, then a CartProductInvalidQuantityException should be thrown")
    void addProductToCart_NotEnoughStockExceptionTest() {

        Cart cart = Cart.builder().id(1L).cartProducts(new ArrayList<>()).build();

        CartProduct cartProduct = CartProduct.builder()
                .id(1L)
                .productId(1L)
                .quantity(1000)
                .cart(cart)
                .build();

        Product product = Product.builder()
                .id(1L)
                .current_stock(100)
                .build();

        lenient().when(productService.getProductById(1L)).thenReturn(product);
        lenient().when(cartRepository.findById(1L)).thenReturn(Optional.of(cart));

        CartProductInvalidQuantityException exception = assertThrows(CartProductInvalidQuantityException.class, () -> {
            cartServiceImpl.addProductToCart(cartProduct);
        });

        assertEquals("Not enough stock to add product to cart. Desired amount: 1000. Actual stock: 100", exception.getMessage());
    }

    @Test
    @DisplayName("Given a cart and a product, when adding the product to the cart, then the product should be added successfully")
    void addProductToCartTest() {
        Cart cart = Cart.builder().id(1L).cartProducts(new ArrayList<>()).build();
        CartProduct cartProduct = CartProduct.builder().id(1L).productId(1L).quantity(10).cart(cart).build();

        when(productService.getProductById(anyLong())).thenReturn(new Product(1L, "prodName", "description", new BigDecimal("100"), 100, 100.0));
        when(cartRepository.findById(1L)).thenReturn(Optional.of(cart));

        cartServiceImpl.addProductToCart(cartProduct);

        verify(cartProductRepository, times(1)).save(any());
    }


    @Test
    @DisplayName("Given a cart and a product, when adding the product to the cart, if there isn't enough stock, an exception should be thrown")
    void addProductToCartTestNotEnoughStock() {

        Cart cart = Cart.builder().id(1L).cartProducts(new ArrayList<>()).build();
        CartProduct cartProduct = CartProduct.builder().id(1L).productId(1L).quantity(1000).cart(cart).build();
        cart.getCartProducts().add(cartProduct);

        when(productService.getProductById(anyLong())).thenReturn(new Product(1L, "prodName", "description", new BigDecimal("100"), 100, 100.0));

        Product product = new Product(1L, "prodName", "description", new BigDecimal("100"), 100, 100.0);
        when(productService.getProductById(1L)).thenReturn(product);
        when(cartRepository.findById(1L)).thenReturn(Optional.of(cart));

        CartProductInvalidQuantityException exception = assertThrows(CartProductInvalidQuantityException.class, () -> {
            cartServiceImpl.getCart(1L);
        });

        assertEquals("Not enough stock. Quantity desired: 1000. Actual stock: 100", exception.getMessage());
    }


    @Test
    @DisplayName("Given a cart with multiple products and a user, " + "when calculating the cart total including tax and weight costs, " + "then the total should be calculated correctly")
    void getCartTotalTest() {
        User user = User.builder().country(new Country(1L, 7.0)).build();

        Product product1 = Product.builder().weight(2.0).build();
        Product product2 = Product.builder().weight(2.0).build();

        Cart cart = Cart.builder().build();

        CartProduct cartProduct1 = CartProduct.builder().productId(1L).price(new BigDecimal("10")).quantity(2).cart(cart).build();

        CartProduct cartProduct2 = CartProduct.builder().productId(2L).price(new BigDecimal("15")).quantity(3).cart(cart).build();

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
        assertEquals(expectedTotal, actualTotal);

        verify(userService).getUserById(1L);
        verify(productService, times(2)).getProductById(anyLong());
        verify(cartRepository).findById(1L);
    }

    @Test
    @DisplayName("Given a total weight, " + "when calculating the weight cost, " + "then the cost should be calculated correctly")
    void calculateWeightCostTest() {

        BigDecimal expectedCost1 = new BigDecimal("50");
        BigDecimal actualCost1 = cartServiceImpl.calculateWeightCost(25);
        assertEquals(expectedCost1, actualCost1);

        BigDecimal expectedCost2 = new BigDecimal("20");
        BigDecimal actualCost2 = cartServiceImpl.calculateWeightCost(15);
        assertEquals(expectedCost2, actualCost2);

        BigDecimal expectedCost3 = new BigDecimal("10");
        BigDecimal actualCost3 = cartServiceImpl.calculateWeightCost(7);
        assertEquals(expectedCost3, actualCost3);

        BigDecimal expectedCost4 = new BigDecimal("5");
        BigDecimal actualCost4 = cartServiceImpl.calculateWeightCost(3);
        assertEquals(expectedCost4, actualCost4);
    }


    @Test
    @DisplayName("Given an existing cart, " + "when clearing the cart, " + "then the cart products should be cleared successfully")
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
    @DisplayName("Given a threshold date, " + "when identifying abandoned carts, " + "then return the list of abandoned carts")
    void identifyAbandonedCartsTest() {

        LocalDate thresholdDate = LocalDate.now().minusDays(2);
        List<Cart> abandonedCarts = new ArrayList<>();
        abandonedCarts.add(Cart.builder().id(1L).build());
        abandonedCarts.add(Cart.builder().id(2L).build());

        when(cartRepository.identifyAbandonedCarts(thresholdDate)).thenReturn(abandonedCarts);

        List<CartDto> result = cartServiceImpl.identifyAbandonedCarts(thresholdDate);

        assertEquals(abandonedCarts.size(), result.size());
    }

    @Test
    @DisplayName("Given a threshold date, " + "when identifying abandoned carts, " + "then an empty list should be returned if there are no abandoned carts")
    void identifyAbandonedCarts_NoAbandonedCartsTest() {
        LocalDate thresholdDate = LocalDate.now().minusDays(2);

        when(cartRepository.identifyAbandonedCarts(thresholdDate)).thenReturn(Collections.emptyList());

        List<CartDto> result = cartServiceImpl.identifyAbandonedCarts(thresholdDate);

        assertTrue(result.isEmpty());
    }


    @Test
    @DisplayName("Given a user id, " + "when creating a cart, " + "then a cart should be created successfully")
    void createCartTest() {
        Long userId = 123L;

        Cart cart = Cart.builder().id(userId).userId(userId).build();

        when(cartRepository.save(any(Cart.class))).thenReturn(cart);

        CartDto createdCart = cartServiceImpl.createCart(userId);

        assertEquals(userId, createdCart.getUserId());
        assertEquals(userId, createdCart.getId());
        verify(cartRepository).save(any(Cart.class));
    }

    @Test
    @DisplayName("Given a valid cart ID, when getting the cart, then return the corresponding cart and calculate the total correctly")
    void getCartTest() {
        Long cartId = 1L;
        Long userId = 1L;
        User user = User.builder().country(new Country(1L, 0.07)).id(userId).build();
        Product product = Product.builder().id(1L).price(new BigDecimal("20")).weight(2.0).current_stock(20).build();
        CartProduct cartProduct = CartProduct.builder().productId(1L).price(new BigDecimal("20")).quantity(2).build();
        Cart cart = Cart.builder().id(cartId).userId(userId).cartProducts(Arrays.asList(cartProduct)).build();

        when(cartRepository.findById(cartId)).thenReturn(Optional.of(cart));
        when(userService.getUserById(userId)).thenReturn(user);
        when(productService.getProductById(1L)).thenReturn(product);

        CartDto result = cartServiceImpl.getCart(cartId);

        assertEquals(cartId, result.getId());
    }


    @Test
    @DisplayName("Given existing carts, " + "when retrieving all carts, " + "then return the list of all carts")
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

        Cart cart = Cart.builder().id(1L).cartProducts(new ArrayList<>()).build();

        CartProduct cartProduct = CartProduct.builder().id(1L).productId(1L).quantity(10).cart(cart).build();

        when(productService.getProductById(anyLong())).thenReturn(new Product(1L, "prodName", "description", new BigDecimal("100"), 100, 100.0));
        when(cartRepository.findById(1L)).thenReturn(Optional.of(cart));


        when(cartRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(CartNotFoundException.class, () -> {
            cartServiceImpl.addProductToCart(cartProduct);
        });
    }

    @Test
    @DisplayName("Given a non-existent cart, " + "when clearing the cart, " + "then a CartNotFoundException should be thrown")
    void clearCart_CartNotFoundExceptionTest() {

        when(cartRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(CartNotFoundException.class, () -> {
            cartServiceImpl.clearCart(1L);
        });
    }

    @Test
    @DisplayName("Given a non-existent user, " + "when calculating the cart total, " + "then a UserNotFoundException should be thrown")
    void getCartTotal_UserNotFoundExceptionTest() {
        Long cartId = 1L;
        Long userId = 1L;

        when(userService.getUserById(userId)).thenThrow(new UserNotFoundException("User with ID " + userId + " not found"));

        assertThrows(UserNotFoundException.class, () -> {
            cartServiceImpl.getCartTotal(cartId, userId);
        });
    }


    @Test
    @DisplayName("Given a non-existent product in the cart, when calculating the cart total, then a CartProductNotFoundException should be thrown")
    void getCartTotal_CartProductNotFoundExceptionTest() {
        Long cartId = 1L;
        Long userId = 1L;

        int quantity = 5;
        CartProduct cartProduct = CartProduct.builder().productId(999L).quantity(quantity).build();

        BigDecimal price = BigDecimal.valueOf(10.0);
        cartProduct.setPrice(price);

        Cart cart = Cart.builder().cartProducts(Collections.singletonList(cartProduct)).build();

        when(cartRepository.findById(cartId)).thenReturn(Optional.of(cart));
        when(userService.getUserById(userId)).thenReturn(User.builder().country(new Country(101L, 7.5)).build());

        when(productService.getProductById(cartProduct.getProductId())).thenThrow(new CartProductNotFoundException("Product with ID " + cartProduct.getProductId() + " not found"));

        assertThrows(CartProductNotFoundException.class, () -> {
            cartServiceImpl.getCartTotal(cartId, userId);
        });
    }


    @Test
    @DisplayName("Given a non-existent cart, " + "when calculating the cart total, " + "then a CartNotFoundException should be thrown")
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
    @DisplayName("Given a user with an existing cart, " + "when creating a new cart, " + "then a UserWithCartException should be thrown")
    void createCart_UserWithCartExceptionTest() {
        Long userId = 123L;

        Cart existingCart = Cart.builder().userId(userId).build();

        when(cartRepository.findByUserId(userId)).thenReturn(Optional.of(existingCart));

        UserWithCartException exception = assertThrows(UserWithCartException.class, () -> {
            cartServiceImpl.createCart(userId);
        });

        assertEquals("User with ID " + userId + " already has a cart.", exception.getMessage());
    }

}
