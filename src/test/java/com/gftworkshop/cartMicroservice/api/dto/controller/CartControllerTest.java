package com.gftworkshop.cartMicroservice.api.dto.controller;

import com.gftworkshop.cartMicroservice.services.impl.CartServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.Mockito.mock;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class CartControllerTest {

    private MockMvc mockMvc;
    private CartController cartController;

    @BeforeEach
    void setUp() {
        CartServiceImpl cartService = mock(CartServiceImpl.class);
        cartController = new CartController(cartService);
        mockMvc = MockMvcBuilders.standaloneSetup(cartController).build();
    }

    @Test
    @DisplayName("When getting cart by ID, then expect OK status")
    void getCartByIdTest() throws Exception {
        Long cartId = 1L;
        mockMvc.perform(get("/carts/{id}", cartId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("When removing cart by ID, then expect OK status")
    void removeCartByIdTest() throws Exception {
        Long cartId = 1L;
        mockMvc.perform(delete("/carts/{id}", cartId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("When adding product, then expect OK status")
    void addProductTest() throws Exception {
        mockMvc.perform(post("/cartProducts")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("When updating product, then expect OK status")
    void updateProductTest() throws Exception {
        mockMvc.perform(patch("/cartProducts")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("When removing product by ID, then expect OK status")
    void removeProductByIdTest() throws Exception{
        Long productId = 1L;
        mockMvc.perform(delete("/cartProducts/{id}", productId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }
}
