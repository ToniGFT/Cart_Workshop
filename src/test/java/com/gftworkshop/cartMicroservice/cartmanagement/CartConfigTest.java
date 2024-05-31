package com.gftworkshop.cartMicroservice.cartmanagement;

import com.gftworkshop.cartMicroservice.repositories.CartProductRepository;
import com.gftworkshop.cartMicroservice.repositories.CartRepository;
import com.gftworkshop.cartMicroservice.services.ProductService;
import com.gftworkshop.cartMicroservice.services.UserService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
@DisplayName("CartConfig Unit Tests")
class CartConfigTest {

    @MockBean
    private CartRepository cartRepository;

    @MockBean
    private CartProductRepository cartProductRepository;

    @MockBean
    private ProductService productService;

    @MockBean
    private UserService userService;

    @Test
    @DisplayName("Test CartManager Bean")
    void testCartManagerBean() {
        // Given
        CartConfig cartConfig = new CartConfig();

        // When
        CartManager cartManager = cartConfig.cartManager(cartRepository, cartProductRepository, productService, userService, null);

        // Then
        assertNotNull(cartManager);
    }

    @Test
    @DisplayName("Test CartValidator Bean")
    void testCartValidatorBean() {
        // Given
        CartConfig cartConfig = new CartConfig();

        // When
        CartValidator cartValidator = cartConfig.cartValidator(productService, cartProductRepository);

        // Then
        assertNotNull(cartValidator);
    }

    @Test
    @DisplayName("Test CartCalculator Bean")
    void testCartCalculatorBean() {
        // Given
        CartConfig cartConfig = new CartConfig();

        // When
        CartCalculator cartCalculator = cartConfig.cartCalculator(productService, userService, cartRepository);

        // Then
        assertNotNull(cartCalculator);
    }
}
