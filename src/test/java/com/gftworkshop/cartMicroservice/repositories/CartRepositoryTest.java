package com.gftworkshop.cartMicroservice.repositories;

import com.gftworkshop.cartMicroservice.model.Cart;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

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
        cart = new Cart();
    }

    @Test
    @DisplayName("When getting a specified id, then I get a cart")
    void findCartByIdTest() {
        Long cartId = 1L;
        cart.setId(cartId);

        when(cartRepository.findById(cartId)).thenReturn(Optional.of(cart));

        Optional<Cart> retrievedCartOptional = cartRepository.findById(cartId);
        assertEquals(cartId, retrievedCartOptional.orElseThrow().getId());
    }

    @Test
    @DisplayName("When getting a specified id, then I delete a cart")
    void removeCartByIdTest() {
        Long cartIdToDelete = 1L;
        cart.setId(cartIdToDelete);

        when(cartRepository.findById(cartIdToDelete)).thenReturn(Optional.of(cart));

        cartRepository.deleteById(cartIdToDelete);
        assertFalse(cartRepository.existsById(cartIdToDelete));
    }

    @Test
    @DisplayName("When getting a specified id, then I add a cart")
    void addCartByIdTest() {
        when(cartRepository.save(cart)).thenReturn(cart);

        Cart savedCart = cartRepository.save(cart);

        assertNotNull(savedCart);
        assertEquals(cart.getId(), savedCart.getId());
    }
}