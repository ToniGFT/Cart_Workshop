package com.gftworkshop.cartMicroservice.api.dto.controller;

import com.gftworkshop.cartMicroservice.model.Cart;
import com.gftworkshop.cartMicroservice.model.CartProduct;
import com.gftworkshop.cartMicroservice.services.impl.CartProductServiceImpl;
import com.gftworkshop.cartMicroservice.services.impl.CartServiceImpl;
import org.junit.jupiter.api.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class CartControllerTest {

    private MockMvc mockMvc;
    private CartController cartController;
    private CartProductServiceImpl cartProductService;
    private CartServiceImpl cartService;
    private Long cartId;
    private Long productId;

    @BeforeEach
    void setUp() {
        cartService = mock(CartServiceImpl.class);
        cartProductService = mock(CartProductServiceImpl.class);
        cartController = new CartController(cartService, cartProductService);
        mockMvc = MockMvcBuilders.standaloneSetup(cartController).build();
        cartId = 1L;
        productId = 1L;
    }

    @Nested
    @DisplayName("Tests for Cart code status")
    class CartCodeStatusTests {

        @Test
        @DisplayName("When adding cart by ID, then expect OK status")
        void addCartByIdTest() throws Exception {
            mockMvc.perform(post("/carts/{id}", cartId)
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk());
        }

        @Test
        @DisplayName("When getting cart by ID, then expect OK status")
        void getCartByIdTest() throws Exception {
            mockMvc.perform(get("/carts/{id}", cartId)
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk());
        }

        @Test
        @DisplayName("When removing cart by ID, then expect OK status")
        void removeCartByIdTest() throws Exception {
            mockMvc.perform(delete("/carts/{id}", cartId)
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk());
        }
    }

    @Nested
    @DisplayName("Tests for CartProduct code status")
    class CartProductCodeStatusTests {

        @Test
        @DisplayName("When adding product, then expect OK status")
        void addProductTest() throws Exception {
            String requestBody = "{"
                    + "\"id\": null,"
                    + "\"cart\": {\"id\": 1},"
                    + "\"productName\": \"product name\","
                    + "\"productCategory\": \"product category\","
                    + "\"productDescription\": \"product description\","
                    + "\"quantity\": 5,"
                    + "\"price\": 10.50"
                    + "}";

            mockMvc.perform(patch("/carts/products")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(requestBody))
                    .andExpect(status().isOk());
        }

        @Test
        @DisplayName("When updating product, then expect OK status")
        void updateProductTest() throws Exception {

            String requestBody = "{"
                    + "\"id\": null,"
                    + "\"cart\": {\"id\": 1},"
                    + "\"productName\": \"product name\","
                    + "\"productCategory\": \"product category\","
                    + "\"productDescription\": \"product description\","
                    + "\"quantity\": 5,"
                    + "\"price\": 10.50"
                    + "}";

            mockMvc.perform(patch("/carts/products")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(requestBody))
                    .andExpect(status().isOk());
        }

        @Test
        @DisplayName("When removing product by ID, then expect OK status")
        void removeProductByIdTest() throws Exception {
            mockMvc.perform(delete("/carts/products/{id}", productId)
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk());
        }
    }

    @Nested
    @DisplayName("Tests for CartProducts operations")
    class CartProductOperationsTests {

        @Test
        @DisplayName("When updating a product, then expect OK status")
        void testUpdateProduct() {
            Long productId = 123L;
            int newQuantity = 5;
            CartProduct cartProduct = new CartProduct();
            cartProduct.setId(productId);
            cartProduct.setQuantity(newQuantity);

            when(cartProductService.updateQuantity(productId, newQuantity)).thenReturn(ResponseEntity.ok().build().getStatusCode().value());

            ResponseEntity<Cart> response = cartController.updateProduct(cartProduct);

            verify(cartProductService, times(1)).updateQuantity(productId, newQuantity);
            assertEquals(HttpStatus.OK, response.getStatusCode());
        }

        @Test
        @DisplayName("When removing a product, then expect OK status")
        void removeProductByIdTest() throws Exception {
            Long productId = 123L;

            ResponseEntity<Cart> response = cartController.removeProductById(productId);

            verify(cartProductService, times(1)).removeProduct(productId);

            assertEquals(HttpStatus.OK, response.getStatusCode());
        }
    }
}
