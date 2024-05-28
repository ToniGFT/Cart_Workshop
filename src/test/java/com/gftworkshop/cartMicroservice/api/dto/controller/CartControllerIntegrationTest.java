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
            String username = "sivaprasadreddy";
            wireMockServer.stubFor(WireMock.get(urlMatching("/carts/.*"))
                    .willReturn(
                            aResponse()
                                    .withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                                    .withBody("""
						{
							"login": "%s",
							"name": "K. Siva Prasad Reddy",
							"twitter_username": "sivalabs",
							"public_repos": 50
						}
						""".formatted(username))));

            mockMvc.perform(get("/api/users/{username}", username))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.login", is(username)))
                    .andExpect(jsonPath("$.name", is("K. Siva Prasad Reddy")))
                    .andExpect(jsonPath("$.public_repos", is(50)));


            //When
//            client.get().uri("/carts/{id}", 1L).exchange()
//                    .expectStatus().isOk()
//                    .expectHeader().contentType(MediaType.APPLICATION_JSON)
//                    .expectBody(CartDto.class)
//                    .value(cartDto -> {
//                        //Then
//                        assertThat(cartDto).isEqualTo(expectedCart);
//                    });

        }
    }

}
