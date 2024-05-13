package com.gftworkshop.cartMicroservice.services;

import com.gftworkshop.cartMicroservice.api.dto.Product;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Service
public class ProductService {
    private final WebClient webClient;

    public ProductService(WebClient webClient) {
        this.webClient = webClient;
    }

    public Mono<Product> getProductById(Long productId) {
        return webClient.get()
                .uri("/catalog/products/{id}", productId)
                .retrieve()
                .bodyToMono(Product.class);
    }
}
