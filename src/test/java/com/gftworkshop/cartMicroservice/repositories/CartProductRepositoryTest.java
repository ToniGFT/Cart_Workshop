package com.gftworkshop.cartMicroservice.repositories;

import com.gftworkshop.cartMicroservice.model.Cart;
import com.gftworkshop.cartMicroservice.model.CartProduct;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@DataJpaTest
public class CartProductRepositoryTest {

    private CartProductRepository cartProductRepository;
    private CartProduct cartProduct;

    @BeforeEach
    void setUp() {
        cartProductRepository = mock(CartProductRepository.class);
        cartProduct = new CartProduct();
    }

}
