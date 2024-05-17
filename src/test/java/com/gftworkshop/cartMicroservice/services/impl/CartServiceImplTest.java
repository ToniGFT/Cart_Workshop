package com.gftworkshop.cartMicroservice.services.impl;

import com.gftworkshop.cartMicroservice.api.dto.CartDto;
import com.gftworkshop.cartMicroservice.api.dto.Country;
import com.gftworkshop.cartMicroservice.api.dto.Product;
import com.gftworkshop.cartMicroservice.api.dto.User;
import com.gftworkshop.cartMicroservice.exceptions.CartNotFoundException;
import com.gftworkshop.cartMicroservice.exceptions.CartProductNotFoundException;
import com.gftworkshop.cartMicroservice.exceptions.UserNotFoundException;
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

import java.math.BigDecimal;
import java.math.RoundingMode;
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
    @DisplayName("Given a cart with multiple products and a user, " +
            "when calculating the cart total including tax and weight costs, " +
            "then the total should be calculated correctly")
    void getCartTotalTest() {
        User user = new User();
        user.setCountry(new Country(1L, 0.07));

        Product product1 = new Product();
        product1.setWeight(2.0);
        Product product2 = new Product();
        product2.setWeight(3.5);

        Cart cart = new Cart();
        CartProduct cartProduct1 = new CartProduct();
        cartProduct1.setProductId(1L);
        cartProduct1.setPrice(new BigDecimal("10"));
        cartProduct1.setQuantity(2);
        cartProduct1.setCart(cart);

        CartProduct cartProduct2 = new CartProduct();
        cartProduct2.setProductId(2L);
        cartProduct2.setPrice(new BigDecimal("15"));
        cartProduct2.setQuantity(3);
        cartProduct2.setCart(cart);

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
    @DisplayName("Given a specific weight, " +
            "when calculating weight cost, " +
            "then it should return te correct cost")
    public void testCalculateWeightCost() {
        assertEquals(new BigDecimal("5"), cartServiceImpl.calculateWeightCost(5));
        assertEquals(new BigDecimal("10"), cartServiceImpl.calculateWeightCost(6));
        assertEquals(new BigDecimal("20"), cartServiceImpl.calculateWeightCost(11));
        assertEquals(new BigDecimal("50"), cartServiceImpl.calculateWeightCost(21));
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

        Cart cart = new Cart();
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

        Cart cart = new Cart();
        CartProduct cartProduct = new CartProduct();
        cartProduct.setProductId(999L);
        cart.setCartProducts(Collections.singletonList(cartProduct));

        when(cartRepository.findById(cartId)).thenReturn(Optional.of(cart));

        when(userService.getUserById(userId)).thenReturn(new User());

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

        when(userService.getUserById(userId)).thenReturn(new User());

        when(cartRepository.findById(cartId)).thenReturn(Optional.empty());

        assertThrows(CartNotFoundException.class, () -> {
            cartServiceImpl.getCartTotal(cartId, userId);
        });
    }

}
