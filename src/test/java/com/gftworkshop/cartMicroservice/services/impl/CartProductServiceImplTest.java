package com.gftworkshop.cartMicroservice.services.impl;

import com.gftworkshop.cartMicroservice.model.CartProduct;
import com.gftworkshop.cartMicroservice.repositories.CartProductRepository;
import com.gftworkshop.cartMicroservice.services.CartProductService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class CartProductServiceImplTest {

    private CartProductRepository cartProductRepository;
    private CartProductService cartProductService;
    private CartProduct cartProduct;

    @BeforeEach
    void setUp() {

        cartProductRepository = mock(CartProductRepository.class);

        cartProductService = new CartProductServiceImpl(cartProductRepository);

        cartProduct = mock(CartProduct.class);

    }

    @Test
    @DisplayName("Save a CartProduct - Given CartProduct " +
            "When Saved " +
            "Then Return Same CartProduct")
    void saveTest() {

        when(cartProductRepository.save(any(CartProduct.class))).thenReturn(cartProduct);

        CartProduct result = cartProductService.save(cartProduct);

        assertEquals(cartProduct, result);
        verify(cartProductRepository).save(any(CartProduct.class));
    }

    @Test
    @DisplayName("Update CartProduct Quantity - Given Product ID and New Quantity " +
            "When Updated " +
            "Then Return Rows Affected")
    void updateQuantityTest() {
        Long id = 123L;
        int newQuantity = 5;

        when(cartProductRepository.updateProductQuantity(id, newQuantity)).thenReturn(1);

        int rowsAffected = cartProductService.updateQuantity(id, newQuantity);

        assertEquals(1, rowsAffected);
        verify(cartProductRepository).updateProductQuantity(id, newQuantity);

    }

    @Test
    @DisplayName("Remove CartProduct - Given Product ID " +
            "When Removed " +
            "Then Verify Deletion")
    void removeProductTest() {

        Long id = 1L;

        doNothing().when(cartProductRepository).deleteById(id);

        cartProductService.removeProduct(id);

        verify(cartProductRepository, times(1)).deleteById(id);
    }
}
