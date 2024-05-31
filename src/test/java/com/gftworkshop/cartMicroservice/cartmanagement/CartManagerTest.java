package com.gftworkshop.cartMicroservice.cartmanagement;

import com.gftworkshop.cartMicroservice.exceptions.CartNotFoundException;
import com.gftworkshop.cartMicroservice.model.Cart;
import com.gftworkshop.cartMicroservice.model.CartProduct;
import com.gftworkshop.cartMicroservice.repositories.CartRepository;
import com.gftworkshop.cartMicroservice.services.ProductService;
import com.gftworkshop.cartMicroservice.services.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@DisplayName("CartManager Unit Tests")
class CartManagerTest {

    @Mock
    private CartRepository cartRepository;

    @Mock
    private ProductService productService;

    @Mock
    private UserService userService;

    @InjectMocks
    private CartManager cartManager;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("Test fetchCartById - Existing Cart")
    void testFetchCartByIdExisting() {
        // Given
        Long cartId = 1L;
        Cart expectedCart = new Cart();
        expectedCart.setId(cartId);
        when(cartRepository.findById(cartId)).thenReturn(java.util.Optional.of(expectedCart));

        // When
        Cart actualCart = cartManager.fetchCartById(cartId);

        // Then
        assertEquals(expectedCart, actualCart);
    }


    @Test
    @DisplayName("Test fetchAllCarts - Empty List")
    void testFetchAllCartsEmpty() {
        // Given
        List<Cart> expectedCarts = new ArrayList<>();
        when(cartRepository.findAll()).thenReturn(expectedCarts);

        // When
        List<Cart> actualCarts = cartManager.fetchAllCarts();

        // Then
        assertEquals(expectedCarts, actualCarts);
    }

    @Test
    @DisplayName("Test fetchAllCarts - Non-empty List")
    void testFetchAllCartsNonEmpty() {
        // Given
        List<Cart> expectedCarts = new ArrayList<>();
        expectedCarts.add(new Cart());
        when(cartRepository.findAll()).thenReturn(expectedCarts);

        // When
        List<Cart> actualCarts = cartManager.fetchAllCarts();

        // Then
        assertEquals(expectedCarts, actualCarts);
    }


    @Test
    @DisplayName("Test fetchCartById - Nonexistent Cart")
    void testFetchCartByIdNonexistent() {
        // Given
        Long cartId = 1L;
        when(cartRepository.findById(cartId)).thenReturn(Optional.empty());

        // When/Then
        assertThrows(CartNotFoundException.class, () -> cartManager.fetchCartById(cartId));
    }

    @Test
    @DisplayName("Test addCartProduct")
    void testAddCartProduct() {
        // Given
        Cart cart = new Cart();
        CartProduct cartProduct = new CartProduct();

        // When
        cartManager.addCartProduct(cart, cartProduct);

        // Then
        assertTrue(cart.getCartProducts().contains(cartProduct));
        assertEquals(cart, cartProduct.getCart());
    }

    @Test
    @DisplayName("Test updateCartTimestamp")
    void testUpdateCartTimestamp() {
        // Given
        Cart cart = new Cart();
        LocalDate initialUpdatedAt = cart.getUpdatedAt();

        // When
        cartManager.updateCartTimestamp(cart);

        // Then
        assertNotEquals(initialUpdatedAt, cart.getUpdatedAt());
        assertEquals(LocalDate.now(), cart.getUpdatedAt());
    }

}
