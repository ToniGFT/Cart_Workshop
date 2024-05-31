package com.gftworkshop.cartMicroservice.cartmanagement;

import com.gftworkshop.cartMicroservice.api.dto.CartProductDto;
import com.gftworkshop.cartMicroservice.api.dto.Country;
import com.gftworkshop.cartMicroservice.api.dto.Product;
import com.gftworkshop.cartMicroservice.api.dto.User;
import com.gftworkshop.cartMicroservice.model.Cart;
import com.gftworkshop.cartMicroservice.model.CartProduct;
import com.gftworkshop.cartMicroservice.repositories.CartRepository;
import com.gftworkshop.cartMicroservice.services.ProductService;
import com.gftworkshop.cartMicroservice.services.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.AbstractMap;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@DisplayName("CartCalculator Unit Tests")
class CartCalculatorTest {

    private ProductService productService;
    private UserService userService;
    private CartRepository cartRepository;
    private CartCalculator cartCalculator;

    @BeforeEach
    void setUp() {
        productService = mock(ProductService.class);
        userService = mock(UserService.class);
        cartRepository = mock(CartRepository.class);
        cartCalculator = new CartCalculator(productService, userService, cartRepository);
    }

    @Test
    @DisplayName("Test calculateCartTotal")
    void testCalculateCartTotal() {
        // Given
        Long cartId = 1L;
        Long userId = 1L;

        User user = new User();
        Country country = new Country(1L, 10.0);
        user.setCountry(country);

        CartProductDto cartProductDto1 = CartProductDto.builder().id(1L).productId(1L).build();
        CartProductDto cartProductDto2 = CartProductDto.builder().id(2L).productId(2L).build();
        List<CartProductDto> cartProductDtos = List.of(cartProductDto1, cartProductDto2);

        Cart cart = new Cart();
        cart.setCartProducts(List.of(
                CartProduct.builder().id(1L).productId(1L).quantity(2).build(),
                CartProduct.builder().id(2L).productId(2L).quantity(1).build()
        ));

        Product product1 = new Product(1L, "Product1", "Description1", BigDecimal.TEN, 2, 2.0);
        Product product2 = new Product(2L, "Product2", "Description2", BigDecimal.valueOf(20), 1, 3.0);
        List<Product> products = List.of(product1, product2);

        when(userService.getUserById(userId)).thenReturn(user);
        when(cartRepository.findById(cartId)).thenReturn(Optional.of(cart));
        when(productService.getProductByIdWithDiscountedPrice(cartProductDtos)).thenReturn(products);

        // Print mocked product prices for debugging
        List<Product> mockedProducts = productService.getProductByIdWithDiscountedPrice(cartProductDtos);
        System.out.println("Mocked product prices: " + mockedProducts.stream().map(Product::getPrice).toList());

        // Total product cost: 10 * 2 + 20 * 1 = 40
        BigDecimal totalProductCost = BigDecimal.valueOf(40.0);
        // Tax: 10% of 40 = 4
        BigDecimal tax = BigDecimal.valueOf(4.0);
        // Shipping cost: weight = 2 * 2 + 3 = 7 -> shipping cost = 10
        BigDecimal shippingCost = BigDecimal.valueOf(10.0);

        BigDecimal expectedTotal = totalProductCost.add(tax).add(shippingCost);

        // When
        BigDecimal totalProductCostCalculated = cartCalculator.computeProductTotal(products);
        System.out.println("Total product cost calculated: " + totalProductCostCalculated);

        BigDecimal taxCalculated = cartCalculator.computeTax(totalProductCostCalculated, user);
        System.out.println("Tax calculated: " + taxCalculated);

        double totalWeight = cartCalculator.computeTotalWeight(products);
        System.out.println("Total weight calculated: " + totalWeight);

        BigDecimal shippingCostCalculated = cartCalculator.computeShippingCost(totalWeight);
        System.out.println("Shipping cost calculated: " + shippingCostCalculated);

        BigDecimal total = cartCalculator.calculateCartTotal(cartId, userId);

        // Then
        assertEquals(expectedTotal, total);
    }


    @Test
    @DisplayName("Test computeProductTotal")
    void testComputeProductTotal() {
        // Given
        List<Product> products = List.of(
                new Product(1L, "Product1", "Description1", BigDecimal.TEN, 5, 2.0),
                new Product(2L, "Product2", "Description2", BigDecimal.valueOf(20), 10, 3.0)
        );
        BigDecimal expectedTotal = BigDecimal.valueOf(30);

        // When
        BigDecimal total = cartCalculator.computeProductTotal(products);

        // Then
        assertEquals(expectedTotal, total);
    }

    @Test
    @DisplayName("Test computeTax")
    void testComputeTax() {
        // Given
        BigDecimal total = BigDecimal.valueOf(100);
        User user = new User();
        user.setCountry(new Country(1L, 10.0));
        BigDecimal expectedTax = BigDecimal.TEN;

        // When
        BigDecimal tax = cartCalculator.computeTax(total, user);

        // Then
        assertEquals(0, expectedTax.compareTo(tax));
    }


    @Test
    @DisplayName("Test computeShippingCost")
    void testComputeShippingCost() {
        // Given
        double totalWeight = 15.0;
        List<AbstractMap.SimpleEntry<Double, BigDecimal>> weightCosts = List.of(
                new AbstractMap.SimpleEntry<>(5.0, BigDecimal.valueOf(5)),
                new AbstractMap.SimpleEntry<>(10.0, BigDecimal.valueOf(10)),
                new AbstractMap.SimpleEntry<>(20.0, BigDecimal.valueOf(20)),
                new AbstractMap.SimpleEntry<>(Double.MAX_VALUE, BigDecimal.valueOf(50))
        );
        BigDecimal expectedShippingCost = BigDecimal.valueOf(20);

        // When
        BigDecimal shippingCost = cartCalculator.findShippingCostForWeight(totalWeight, weightCosts);

        // Then
        assertEquals(expectedShippingCost, shippingCost);
    }

    @Test
    @DisplayName("Test computeTotalWeight")
    void testComputeTotalWeight() {
        // Given
        List<Product> products = List.of(
                new Product(1L, "Product1", "Description1", BigDecimal.TEN, 5, 2.0),
                new Product(2L, "Product2", "Description2", BigDecimal.valueOf(20), 10, 3.0)
        );
        double expectedTotalWeight = 5.0;

        // When
        double totalWeight = cartCalculator.computeTotalWeight(products);

        // Then
        assertEquals(expectedTotalWeight, totalWeight);
    }
}
