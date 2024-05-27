package com.gftworkshop.cartMicroservice.services;

import com.gftworkshop.cartMicroservice.api.dto.Product;
import com.gftworkshop.cartMicroservice.exceptions.ExternalMicroserviceException;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.List;
import java.util.Map;

@Service
public class ProductService {
    public String productUrl = "http://localhost:8081/catalog/products/{id}";
    public String productsUrl = "http://localhost:8081/catalog/productsWithDiscount";
    public String discountUrl = "http://localhost:8081/catalog/products/{product_id}/price-checkout?quantity={quantity}";
    private final RestClient restClient;

    public ProductService(RestClient restClient) {
        this.restClient = restClient;
    }

    public Product getProductById(Long productId) {
        return restClient.get()
                .uri(productUrl, productId)
                .retrieve()
                .onStatus(HttpStatusCode::isError, ((request, response) -> {
                    throw new ExternalMicroserviceException("CATALOG MICROSERVICE EXCEPTION: " + response.getStatusText()+" "+response.getBody());
                }))
                .body(Product.class);
    }

    public float getProductDiscountedPrice(Long productId, int quantity) {
        return restClient.get()
                .uri(discountUrl, productId, quantity)
                .retrieve()
                .onStatus(HttpStatusCode::isError, ((request, response) -> {
                    throw new ExternalMicroserviceException("CATALOG MICROSERVICE EXCEPTION: " + response.getStatusText()+" "+response.getBody());
                }))
                .body(Float.class);
    }

    public List<Product> getProductByIdWithDiscountedPrice(Map<Long, Integer> productIdAmountMap) {
        ResponseEntity<List<Product>> responseEntity = restClient.post()
                .uri(productsUrl, productIdAmountMap)
                .body(productIdAmountMap)
                .retrieve()
                .onStatus(HttpStatusCode::isError, ((request,response) -> {
                    throw new ExternalMicroserviceException("CATALOG MICROSERVICE EXCEPTION: " + response.getStatusText()+" "+response.getBody());
                }))
                .toEntity(new ParameterizedTypeReference<List<Product>>() {});
        return responseEntity.getBody();
    }
}
