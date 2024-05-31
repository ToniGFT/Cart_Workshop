package com.gftworkshop.cartMicroservice.cartmanagement;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DisplayName("CartErrorMessages Unit Tests")
class CartErrorMessagesTest {

    @Test
    @DisplayName("Test NOT_ENOUGH_STOCK message")
    void testNotEnoughStockMessage() {
        // Given
        int desiredAmount = 5;
        String expectedMessage = "Not enough stock to add product to cart. Desired amount: 5";

        // When
        String actualMessage = CartErrorMessages.NOT_ENOUGH_STOCK + desiredAmount;

        // Then
        assertEquals(expectedMessage, actualMessage);
    }

    @Test
    @DisplayName("Test ACTUAL_STOCK message")
    void testActualStockMessage() {
        // Given
        int actualStock = 10;
        String expectedMessage = ". Actual stock: 10";

        // When
        String actualMessage = CartErrorMessages.ACTUAL_STOCK + actualStock;

        // Then
        assertEquals(expectedMessage, actualMessage);
    }

    @Test
    @DisplayName("Test USER_ALREADY_HAS_CART message")
    void testUserAlreadyHasCartMessage() {
        // Given
        String expectedMessage = "User already has a cart.";

        // When
        String actualMessage = CartErrorMessages.USER_ALREADY_HAS_CART;

        // Then
        assertEquals(expectedMessage, actualMessage);
    }

    @Test
    @DisplayName("Test NO_ABANDONED_CARTS_FOUND message")
    void testNoAbandonedCartsFoundMessage() {
        // Given
        String timestamp = "2024-05-31 10:00:00";
        String expectedMessage = "No abandoned carts found before 2024-05-31 10:00:00";

        // When
        String actualMessage = CartErrorMessages.NO_ABANDONED_CARTS_FOUND + timestamp;

        // Then
        assertEquals(expectedMessage, actualMessage);
    }

    @Test
    @DisplayName("Test FOUND_ABANDONED_CARTS message")
    void testFoundAbandonedCartsMessage() {
        // Given
        int abandonedCartsCount = 2;
        String timestamp = "2024-05-31 10:00:00";
        String expectedMessage = "Found 2 abandoned carts before 2024-05-31 10:00:00";

        // When
        String actualMessage = CartErrorMessages.FOUND_ABANDONED_CARTS.replace("{}", String.valueOf(abandonedCartsCount)) + timestamp;

        // Then
        assertEquals(expectedMessage, actualMessage);
    }

    @Test
    @DisplayName("Test ABANDONED_CART message")
    void testAbandonedCartMessage() {
        // Given
        String cartInfo = "Cart ID: 123, User ID: 456";
        String timestamp = "2024-05-31 10:00:00";
        String expectedMessage = "Abandoned cart: Cart ID: 123, User ID: 456, at 2024-05-31 10:00:00";

        // When
        String actualMessage = CartErrorMessages.ABANDONED_CART.replace("{}", String.valueOf(cartInfo)) + timestamp;

        // Then
        assertEquals(expectedMessage, actualMessage);
    }
}
