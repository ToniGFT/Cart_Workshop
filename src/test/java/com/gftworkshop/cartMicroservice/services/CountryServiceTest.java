package com.gftworkshop.cartMicroservice.services;

import com.gftworkshop.cartMicroservice.api.dto.Country;
import com.gftworkshop.cartMicroservice.api.dto.Product;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.AfterEach;
import org.springframework.http.HttpStatus;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
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
    @DisplayName("When fetching a country by ID, " +
            "then the correct country details are returned")
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
<<<<<<< HEAD
                                country.getName().equals("Spain") &&
=======
>>>>>>> main
                                country.getTax().equals(21.0))
                .verifyComplete();
    }

    @Test
<<<<<<< HEAD
    @DisplayName("When fetching a country by ID and the country does not exist, then a 404 Not Found error is returned")
=======
    @DisplayName("When fetching a country by ID and the country does not exist," +
            " then a 404 Not Found error is returned")
>>>>>>> main
    void testGetCountryByIdServerError() {
        mockWebServer.enqueue(new MockResponse()
                .setResponseCode(404)
                .setBody("Not found error")
                .addHeader("Content-Type", "text/plain"));

        Mono<Country> countryMono = countryService.getCountryById(1L);

        StepVerifier.create(countryMono)
                .expectErrorMatches(throwable ->
                        throwable instanceof WebClientResponseException &&
                                ((WebClientResponseException) throwable).getStatusCode() == HttpStatus.NOT_FOUND)
                .verify();
    }

    @Test
<<<<<<< HEAD
    @DisplayName("When fetching a Country by ID and an internal server error occurs, then a 500 error is returned")
=======
    @DisplayName("When fetching a Country by ID and an internal server error occurs," +
            " then a 500 error is returned")
>>>>>>> main
    void testGetProductByIdServerError() {
        mockWebServer.enqueue(new MockResponse()
                .setResponseCode(500)
                .setBody("Internal Server Error")
                .addHeader("Content-Type", "text/plain"));

        Mono<Country> countryMono = countryService.getCountryById(1L);

        StepVerifier.create(countryMono)
                .expectErrorMatches(throwable ->
                        throwable instanceof WebClientResponseException &&
                                ((WebClientResponseException) throwable).getStatusCode() == HttpStatus.INTERNAL_SERVER_ERROR)
                .verify();
    }


    @AfterEach
    void tearDown() throws IOException {
        mockWebServer.shutdown();
    }
}
