package com.gftworkshop.cartMicroservice.services.impl;

import com.gftworkshop.cartMicroservice.model.CartProduct;
import com.gftworkshop.cartMicroservice.repositories.CartProductRepository;
import com.gftworkshop.cartMicroservice.services.CartProductService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
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
    void testSave() {

        when(cartProductRepository.save(any(CartProduct.class))).thenReturn(cartProduct);

        CartProduct result = cartProductService.save(cartProduct);

        assertEquals(cartProduct, result);
        verify(cartProductRepository).save(any(CartProduct.class));
    }

}
