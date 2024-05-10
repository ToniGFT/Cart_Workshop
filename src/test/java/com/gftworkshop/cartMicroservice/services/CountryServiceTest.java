package com.gftworkshop.cartMicroservice.services;

import com.gftworkshop.cartMicroservice.api.dto.Country;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.AfterEach;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.MockResponse;

import java.io.IOException;

public class CountryServiceTest {

    private MockWebServer mockWebServer;
    private CountryService countryService;

    @BeforeEach
    void setUp() throws IOException {
        mockWebServer = new MockWebServer();
        mockWebServer.start();
        WebClient webClient = WebClient.builder()
                .baseUrl(mockWebServer.url("/").toString())
                .build();
        countryService = new CountryService(webClient);
    }

    @Test
    @DisplayName("When fetching a country by ID, then the correct country details are returned")
    void testGetCountryById() {
        String countryJson = """
                {
                    "id": 1,
                    "name": "Spain",
                    "tax": 21.0,
                    "prefix": "+34",
                    "timeZone": "CET"
                }
                """;

        mockWebServer.enqueue(new MockResponse()
                .setBody(countryJson)
                .addHeader("Content-Type", "application/json"));

        Mono<Country> countryMono = countryService.getCountryById(1L);

        StepVerifier.create(countryMono)
                .expectNextMatches(country ->
                        country.getId().equals(1L) &&
                                country.getName().equals("Spain") &&
                                country.getTax().equals(21.0))
                .verifyComplete();
    }

    @AfterEach
    void tearDown() throws IOException {
        mockWebServer.shutdown();
    }
}
