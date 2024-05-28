package com.gftworkshop.cartMicroservice.api.dto.controller;

import com.gftworkshop.cartMicroservice.api.dto.CartDto;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.junit5.WireMockExtension;
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
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.test.web.servlet.MockMvc;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.urlMatching;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.hamcrest.CoreMatchers.is;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@ActiveProfiles("dev")
public class CartControllerIntegrationTest {

    @Autowired
    protected MockMvc mockMvc;

    @Autowired
    private WebTestClient client;

    @RegisterExtension
    static WireMockExtension wireMockServer = WireMockExtension.newInstance()
            .options(wireMockConfig().dynamicPort())
            .build();

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("catalog.api.base-url", wireMockServer::baseUrl);
    }

    @Nested
    @DisplayName("GET - Tests for getting a cart by id")
    class GetCartByIdEndpoint {
        @Test
        void getCartByIdTest() throws Exception{
            Long id = 1L;
            wireMockServer.stubFor(WireMock.get(urlMatching("/catalog/products/.*"))
                    .willReturn(
                            aResponse()
                                    .withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                                    .withBody("""
                                    {
                                        "id": 1,
                                        "name": "Product1",
                                        "description": "Description1",
                                        "price": 10.0,
                                        "current_stock": 100,
                                        "weight": 1.0
                                    }
                                    """)));

            mockMvc.perform(get("/carts/{id}", id))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id", is(id.intValue())))
                    .andExpect(jsonPath("$.totalPrice", is(95.2297)));


        }
    }

}
