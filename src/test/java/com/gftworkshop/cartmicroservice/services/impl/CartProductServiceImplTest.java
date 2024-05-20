package com.gftworkshop.cartmicroservice.services.impl;

import com.gftworkshop.cartmicroservice.exceptions.CartNotFoundException;
import com.gftworkshop.cartmicroservice.api.dto.CartProductDto;
import com.gftworkshop.cartmicroservice.exceptions.CartProductInvalidQuantityException;
import com.gftworkshop.cartmicroservice.exceptions.CartProductNotFoundException;
import com.gftworkshop.cartmicroservice.model.Cart;
import com.gftworkshop.cartmicroservice.model.CartProduct;
import com.gftworkshop.cartmicroservice.repositories.CartProductRepository;
import com.gftworkshop.cartmicroservice.repositories.CartRepository;
import com.gftworkshop.cartmicroservice.services.CartService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CartProductServiceImplTest {

    @Mock
    private CartProductRepository cartProductRepository;
    @Mock private CartRepository cartRepository;
    @Mock
    private CartService cartService;
    @InjectMocks
    private CartProductServiceImpl cartProductService;

    @InjectMocks
    private CartServiceImpl cartServiceImpl;
    private CartProduct cartProduct;
    private Long id;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        id = 123L;
        cartProduct = mock(CartProduct.class);

    }

    @Nested
    @DisplayName("Update CartProduct Quantity")
    class UpdateCartProductQuantityTests {
        @Test
        @DisplayName("Given Product ID and New Quantity " +
                "When Updated " +
                "Then Return Rows Affected")
        void updateQuantityTest() {
            long id = 123L;
            int newQuantity = 5;

            when(cartRepository.findById(id)).thenReturn(Optional.of(new Cart()));

            when(cartProductRepository.updateQuantity(id, newQuantity)).thenReturn(1);

            int rowsAffected = cartProductService.updateQuantity(id, newQuantity);

            assertEquals(1, rowsAffected);
            verify(cartProductRepository).updateQuantity(id, newQuantity);
        }

        @Test
        @DisplayName("Given Invalid Quantity " +
                "Then Throws Exception")
        void testUpdateQuantityWithInvalidQuantity() {
            int quantity = -5;

            CartProductInvalidQuantityException exception = assertThrows(CartProductInvalidQuantityException.class, () -> {
                cartProductService.updateQuantity(id, quantity);
            });

            assertEquals("The quantity must be higher than 0", exception.getMessage());

            verifyNoInteractions(cartProductRepository);
        }

        @Test
        @DisplayName("Same Quantity " +
                "When Updated " +
                "Then Return 0 Rows Affected")
        void updateQuantityNoChangesTest() {
            long id = 123L;
            int currentQuantity = 5;

            when(cartRepository.findById(id)).thenReturn(Optional.of(new Cart()));

            when(cartProductRepository.updateQuantity(id, currentQuantity)).thenReturn(0);

            int rowsAffected = cartProductService.updateQuantity(id, currentQuantity);

            assertEquals(0, rowsAffected);
            verify(cartProductRepository).updateQuantity(id, currentQuantity);
        }

        @Test
        @DisplayName("Given Valid Quantity " +
                "When Updated " +
                "Then Return Updated Quantity")
        public void testUpdateQuantity_ValidQuantity_Success() {
            Long id = 1L;
            int quantity = 5;

            Cart cart = new Cart();
            when(cartRepository.findById(id)).thenReturn(Optional.of(cart));
            when(cartProductRepository.updateQuantity(id, quantity)).thenReturn(quantity);

            int updatedQuantity = cartProductService.updateQuantity(id, quantity);

            assertEquals(quantity, updatedQuantity);
            verify(cartRepository, times(1)).findById(id);
            verify(cartProductRepository, times(1)).updateQuantity(id, quantity);
        }

        @Test
        @DisplayName("Given Invalid Quantity Zero " +
                "Then Throws Exception")
        public void testUpdateQuantity_InvalidQuantity_Zero() {
            Long id = 1L;
            int quantity = 0;

            assertThrows(CartProductInvalidQuantityException.class, () -> {
                cartProductService.updateQuantity(id, quantity);
            });

            verify(cartRepository, never()).findById(anyLong());
            verify(cartProductRepository, never()).updateQuantity(anyLong(), anyInt());
        }

        @Test
        @DisplayName("Given Invalid Quantity Negative " +
                "Then Throws Exception")
        public void testUpdateQuantity_InvalidQuantity_Negative() {
            Long id = 1L;
            int quantity = -5;

            assertThrows(CartProductInvalidQuantityException.class, () -> {
                cartProductService.updateQuantity(id, quantity);
            });

            verify(cartRepository, never()).findById(anyLong());
            verify(cartProductRepository, never()).updateQuantity(anyLong(), anyInt());
        }

        @Test
        @DisplayName("Given Nonexistent Cart " +
                "Then Throws Exception")
        public void testUpdateQuantity_CartNotFound() {
            Long id = 999L;
            int quantity = 5;

            when(cartRepository.findById(id)).thenReturn(Optional.empty());

            assertThrows(CartNotFoundException.class, () -> {
                cartProductService.updateQuantity(id, quantity);
            });

            verify(cartProductRepository, never()).updateQuantity(anyLong(), anyInt());
        }
    }


    @Nested
    @DisplayName("Remove CartProduct")
    class RemoveCartProductTests {
        @Test
        @DisplayName("When removing existing CartProduct, then verify deletion and returned value")
        void removeProductTest() {
            CartProduct cartProductToRemove = new CartProduct();
            cartProductToRemove.setId(id); // Asegúrate de establecer el ID u otras propiedades necesarias
            when(cartProductRepository.findById(id)).thenReturn(Optional.of(cartProductToRemove));

            CartProductDto removedProduct = cartProductService.removeProduct(id);

            verify(cartProductRepository, times(1)).deleteById(id);
            assertEquals(cartProductToRemove.getId(), removedProduct.getId());
        }

        @Test
        @DisplayName("When removing non-existent CartProduct - Then verify exception")
        void removeNonExistentProductTest() {

            when(cartProductRepository.findById(id)).thenReturn(Optional.empty());

            CartProductNotFoundException exception = assertThrows(CartProductNotFoundException.class, () -> {
                cartProductService.removeProduct(id);
            });

            assertEquals("No se encontró el CartProduct con ID: " + id, exception.getMessage());
        }
    }


}