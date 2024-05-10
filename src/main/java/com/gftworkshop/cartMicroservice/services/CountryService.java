package com.gftworkshop.cartMicroservice.services;

import com.gftworkshop.cartMicroservice.api.dto.Country;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Service
public class CountryService {

    private final WebClient webClient;

    public CountryService(WebClient webClient) {
        this.webClient = webClient;
    }

    public Mono<Country> getCountryById(Long countryId) {
        return webClient.get()
                .uri("/country/{id}", countryId)
                .retrieve()
                .bodyToMono(Country.class);
    }
}
