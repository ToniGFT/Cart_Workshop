package com.gftworkshop.cartMicroservice.services;

import com.gftworkshop.cartMicroservice.api.dto.Product;
import com.gftworkshop.cartMicroservice.exceptions.ExternalMicroserviceException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

@Service
public class ProductService {
    private final RestClient restClient;
    private final String baseUrl;
    private final String productUri;
    private final String discountUri;

    public ProductService(RestClient restClient,
                          @Value("${catalog.api.base-url}") String baseUrl,
                          @Value("${catalog.api.product-uri}") String productUri,
                          @Value("${catalog.api.discount-uri}") String discountUri) {
        this.restClient = restClient;
        this.baseUrl = baseUrl;
        this.productUri = productUri;
        this.discountUri = discountUri;
    }

    public Product getProductById(Long productId) {
        return restClient.get()
                .uri(baseUrl + productUri, productId)
                .retrieve()
                .onStatus(HttpStatusCode::isError, ((request, response) -> {
                    throw new ExternalMicroserviceException("CATALOG MICROSERVICE EXCEPTION: " + response.getStatusText()+" "+response.getBody());
                }))
                .body(Product.class);
    }

    public float getProductDiscountedPrice(Long productId, int quantity) {
        return restClient.get()
                .uri(discountUri, productId, quantity)
                .retrieve()
                .onStatus(HttpStatusCode::isError, ((request, response) -> {
                    throw new ExternalMicroserviceException("CATALOG MICROSERVICE EXCEPTION: " + response.getStatusText()+" "+response.getBody());
                }))
                .body(Float.class);
    }
}
