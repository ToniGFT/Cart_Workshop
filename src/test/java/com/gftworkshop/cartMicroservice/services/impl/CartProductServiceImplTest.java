package com.gftworkshop.cartMicroservice.services.impl;

import com.gftworkshop.cartMicroservice.exceptions.CartProductSaveException;
import com.gftworkshop.cartMicroservice.model.CartProduct;
import com.gftworkshop.cartMicroservice.repositories.CartProductRepository;
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

public class CartProductServiceImplTest {

    @Mock
    private CartProductRepository cartProductRepository;
    @InjectMocks
    private CartProductServiceImpl cartProductService;
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
            int newQuantity = 5;

            when(cartProductRepository.updateQuantity(id, newQuantity)).thenReturn(1);

            int rowsAffected = cartProductService.updateQuantity(id, newQuantity);

            assertEquals(1, rowsAffected);
            verify(cartProductRepository).updateQuantity(id, newQuantity);

        }

        @Test
        @DisplayName("Given Invalid Quantity " +
                "Then Throws Exception")
        public void testUpdateQuantityWithInvalidQuantity() {
            Long id = 1L;
            int quantity = -5;

            CartProductSaveException exception = assertThrows(CartProductSaveException.class, () -> {
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
            Long id = 1L;
            int currentQuantity = 5;

            when(cartProductRepository.updateQuantity(id, currentQuantity)).thenReturn(0);

            int rowsAffected = cartProductService.updateQuantity(id, currentQuantity);

            assertEquals(0, rowsAffected);
            verify(cartProductRepository).updateQuantity(id, currentQuantity);
        }
    }


    @Test
    @DisplayName("Remove CartProduct - Given Product ID " +
            "When Removed " +
            "Then Verify Deletion")
    void removeProductTest() {
        CartProduct cartProductToRemove = new CartProduct();

        when(cartProductRepository.findById(id)).thenReturn(Optional.of(cartProductToRemove));

        CartProduct removedProduct = cartProductService.removeProduct(id);

        verify(cartProductRepository, times(1)).deleteById(id);

        assertEquals(cartProductToRemove, removedProduct);
    }

}
