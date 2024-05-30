package com.gftworkshop.cartMicroservice.api.dto.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.junit5.WireMockExtension;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.urlMatching;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;
import static org.hamcrest.CoreMatchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathMatching;


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@AutoConfigureMockMvc
@ActiveProfiles("dev")
public class CartControllerIntegrationTest {

    @Autowired
    protected MockMvc mockMvc;

    ObjectMapper objectMapper;

    @RegisterExtension
    static WireMockExtension wireMockServer = WireMockExtension.newInstance()
            .options(wireMockConfig().dynamicPort())
            .build();

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("http://localhost:8080", wireMockServer::baseUrl);
        registry.add("users.api.base-url", () -> "https://user-microservice-ey3npq3qvq-uc.a.run.app");
    }

    private Long userId;
    private Long productId;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();

        userId = 1L;
        wireMockServer.stubFor(WireMock.get(urlMatching("/users/.*"))
                .willReturn(
                        aResponse()
                                .withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                                .withBody("""
                {
                    "id": %d,
                    "username": "john_doe",
                    "email": "john@example.com"
                }
                """.formatted(userId))));

        productId = 1L;

        wireMockServer.stubFor(WireMock.get(urlMatching("/catalog/products/" + productId))
                .willReturn(
                        aResponse()
                                .withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                                .withBody("""
                                {
                                    "id": %d,
                                    "name": "Product1",
                                    "description": "Description1",
                                    "price": 10.0,
                                    "currentStock": 100,
                                    "weight": 1.0
                                }
                                """.formatted(productId))));

        wireMockServer.stubFor(WireMock.post(urlMatching("/catalog/products/byIds"))
                .willReturn(
                        aResponse()
                                .withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                                .withBody("""
                            [
                                {
                                    "id": 1,
                                    "name": "Product1",
                                    "description": "Description1",
                                    "price": 10.0,
                                    "currentStock": 100,
                                    "weight": 1.0
                                }
                            ]
                            """)));

        wireMockServer.stubFor(WireMock.get(urlMatching("/catalog/products/volumePromotion"))
                .willReturn(
                        aResponse()
                                .withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                                .withBody("""
                                {
                                      "id": 1,
                                      "name": "string",
                                      "description": "string",
                                      "price": 10.0,
                                      "categoryId": 0,
                                      "weight": 0,
                                      "currentStock": 100,
                                      "minStock": 0
                                }
                                """)));

    }

    @Nested
    @DisplayName("GET - Tests for getting a cart by id")
    class GetCartByIdEndpoint {
        @Test
        void getCartByIdTest() throws Exception {
            userId = 1L;
            wireMockServer.stubFor(WireMock.get(urlMatching("/users/.*"))
                    .willReturn(
                            aResponse()
                                    .withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                                    .withBody("""
                {
                    "id": %d,
                    "username": "john_doe",
                    "email": "john@example.com"
                }
                """.formatted(userId))));

            productId = 1L;

            wireMockServer.stubFor(WireMock.post(urlMatching("/catalog/products/byIds"))
                    .willReturn(
                            aResponse()
                                    .withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                                    .withBody("""
                            [
                                {
                                    "id": 1,
                                    "name": "Product1",
                                    "description": "Description1",
                                    "price": 10.0,
                                    "currentStock": 100,
                                    "weight": 1.0
                                }
                            ]
                            """)));

            mockMvc.perform(get("/carts/{id}", productId))
                    .andExpect(status().isOk());
        }

        @Test
        void getCartByIdBadRequestTest() throws Exception {
            String invalidId = "abc";

            mockMvc.perform(get("/carts/{id}", invalidId))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.code", is(400)))
                    .andExpect(jsonPath("$.message", is("Invalid input")));
        }

        @Test
        void getCartByIdNotFoundTest() throws Exception {
            Long nonExistentId = 999L;

            wireMockServer.stubFor(WireMock.get(urlMatching("/catalog/products/.*"))
                    .willReturn(aResponse().withStatus(404)));

            mockMvc.perform(get("/carts/{id}", nonExistentId))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.code", is(404)))
                    .andExpect(jsonPath("$.message", is("Cart with ID " + nonExistentId + " not found")));
        }
    }

    @Nested
    @DisplayName("DELETE - Test for removing a cart by id")
    class RemoveCartByIdEndpoint {

        @Test
        void removeCartByIdTest() throws Exception {
            Long cartId = 1L;

            mockMvc.perform(delete("/carts/{id}", cartId))
                    .andExpect(status().isOk());
        }

        @Test
        void removeCartById_NotFoundTest() throws Exception {
            Long nonExistentId = 999L;

            mockMvc.perform(delete("/carts/{id}", nonExistentId))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.code", is(404)))
                    .andExpect(jsonPath("$.message", is("Cart with ID " + nonExistentId + " not found")));
        }

        @Test
        void removeCartById_BadRequest_StringTest() throws Exception {
            String invalidId = "abc";

            mockMvc.perform(delete("/carts/{id}", invalidId))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.code", is(400)))
                    .andExpect(jsonPath("$.message", is("Invalid input")));
        }

        @Test
        void removeCartById_BadRequest_DoubleTest() throws Exception {
            double invalidId = 2.5;

            mockMvc.perform(delete("/carts/{id}", invalidId))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.code", is(400)))
                    .andExpect(jsonPath("$.message", is("Invalid input")));
        }
    }

    @Nested
    @DisplayName("DELETE - Delete product by id")
    class RemoveProductByIdEndpoint {
        @Test
        void removeCartProductByIdTest() throws Exception {
            Long cartProductId = 1L;

            mockMvc.perform(delete("/carts/products/{id}", cartProductId))
                    .andExpect(status().isOk());
        }

        @Test
        void removeCartProductById_NotFoundTest() throws Exception {
            Long cartProductId = 9999L;

            mockMvc.perform(delete("/carts/products/{id}", cartProductId))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.code", is(404)))
                    .andExpect(jsonPath("$.message", is("No se encontró el CartProduct con ID: " + cartProductId)));

        }

        @Test
        void removeCartProductById_BadRequest_StringTest() throws Exception {
            String invalidId = "abc";

            mockMvc.perform(delete("/carts/products/{id}", invalidId))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.code", is(400)))
                    .andExpect(jsonPath("$.message", is("Invalid input")));
        }

        @Test
        void removeCartProductById_BadRequest_DoubleTest() throws Exception {
            double invalidId = 2.5;

            mockMvc.perform(delete("/carts/products/{id}", invalidId))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.code", is(400)))
                    .andExpect(jsonPath("$.message", is("Invalid input")));
        }
    }

    @Nested
    @DisplayName("POST - Tests for adding a cart by user id")
    class AddCartByUserIdEndpoint {

        @Test
        void addCartByUserIdTest() throws Exception {
            Long userId = 4L;
            wireMockServer.stubFor(WireMock.get(urlMatching("/users/.*"))
                    .willReturn(
                            aResponse()
                                    .withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                                    .withBody("""
                                            {
                                                "id": %d,
                                                "username": "john_doe",
                                                "email": "john@example.com"
                                            }
                                            """.formatted(userId))));

            mockMvc.perform(post("/carts/{id}", userId)
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.userId", is(userId.intValue())))
                    .andExpect(jsonPath("$.cartProducts").isEmpty());
        }

        @Test
        void addCartByUserId_BadRequest_StringTest() throws Exception {
            String userId = "badFormatId";

            mockMvc.perform(post("/carts/{id}", userId)
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.code", is(400)))
                    .andExpect(jsonPath("$.message", is("Invalid input")));
        }

        @Test
        void addCartByUserId_BadRequest_DoubleTest() throws Exception {
            Double userId = 1.1;

            mockMvc.perform(post("/carts/{id}", userId)
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.code", is(400)))
                    .andExpect(jsonPath("$.message", is("Invalid input")));
        }
    }

    @Nested
    @DisplayName("POST - Tests for adding a product to the cart")
    class AddProductToCartIdEndpoint {

        @Test
        void postCartProductTest() throws Exception {
            String cartProductJson = """
                    {
                      "cart": {
                        "id": 1
                      },
                      "productId": 1,
                      "productName": "Pride and Prejudice",
                      "productCategory": "Books",
                      "productDescription": "Book by Jane Austen",
                      "quantity": 10,
                      "price": 20.00
                    }""";

            //When
            mockMvc.perform(post("/carts/products")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(cartProductJson))

                    //Then
                    .andExpect(status().isCreated());
        }
    }
}
