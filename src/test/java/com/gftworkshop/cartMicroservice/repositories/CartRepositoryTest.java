package com.gftworkshop.cartMicroservice.repositories;

import com.gftworkshop.cartMicroservice.model.Cart;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@DataJpaTest
public class CartRepositoryTest {

    private CartRepository cartRepository;
    private Cart cart;

    @BeforeEach
    void setUp() {
        cartRepository = mock(CartRepository.class);
        cart = mock(Cart.class);
    }

    @Test
    @DisplayName("When getting a specified id, then I get a cart")
    void findCartByIdTest() {
        Long cartId = 1L;

        when(cart.getId()).thenReturn(cartId);
        when(cartRepository.findById(cartId)).thenReturn(Optional.of(cart));

        Optional<Cart> retrievedCartOptional = cartRepository.findById(cartId);
        assertTrue(retrievedCartOptional.isPresent());
        assertEquals(cartId, retrievedCartOptional.get().getId());
    }

    @Test
    @DisplayName("When getting a specified id, then I delete a cart")
    void removeCartByIdTest() {
        Long cartIdToDelete = 1L;

        when(cart.getId()).thenReturn(cartIdToDelete);
        when(cartRepository.findById(cartIdToDelete)).thenReturn(Optional.of(cart));

        cartRepository.deleteById(cartIdToDelete);
        assertFalse(cartRepository.existsById(cartIdToDelete));
    }

    @Test
    @DisplayName("When getting a specified id, then I add a cart")
    void addCartByIdTest() {
        Long cartId = 1L;

        when(cart.getId()).thenReturn(cartId);
        when(cartRepository.save(cart)).thenReturn(cart);

        Cart savedCart = cartRepository.save(cart);

        assertNotNull(savedCart);
        assertEquals(cartId, savedCart.getId());
    }

    @Test
    @DisplayName("When identifying abandoned carts, " +
            "then return the correct list")
    void identifyAbandonedCartsTest() {
        Date thresholdDate = new Date();
        List<Cart> expectedAbandonedCarts = List.of(mock(Cart.class), mock(Cart.class));

        when(cartRepository.identifyAbandonedCarts(thresholdDate)).thenReturn(expectedAbandonedCarts);

        List<Cart> actualAbandonedCarts = cartRepository.identifyAbandonedCarts(thresholdDate);

        assertEquals(expectedAbandonedCarts, actualAbandonedCarts);
    }
}
