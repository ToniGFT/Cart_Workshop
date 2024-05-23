package com.gftworkshop.cartMicroservice.services;

import com.gftworkshop.cartMicroservice.api.dto.Product;
import com.gftworkshop.cartMicroservice.exceptions.ExternalMicroserviceException;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

@Service
public class ProductService {
    public String endpointUri = "http://localhost:8081/catalog/products/{id}";
    public String discountUri = "http://localhost:8081/catalog/products/{product_id}/price-checkout?quantity={quantity}";
    private final RestClient restClient;

    public ProductService(RestClient restClient) {
        this.restClient = restClient;
    }

    public Product getProductById(Long productId) {
        return restClient.get()
                .uri(endpointUri, productId)
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
