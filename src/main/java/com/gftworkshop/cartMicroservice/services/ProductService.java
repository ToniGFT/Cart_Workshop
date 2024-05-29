package com.gftworkshop.cartMicroservice.services;

import com.gftworkshop.cartMicroservice.api.dto.CartProductDto;
import com.gftworkshop.cartMicroservice.api.dto.Product;
import com.gftworkshop.cartMicroservice.exceptions.ExternalMicroserviceException;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.List;
import java.util.Objects;

@Service
public class ProductService {
    public String productUrl = "http://localhost:8081/catalog/products/{id}";
    public String productsUrl = "http://localhost:8081/catalog/productsWithDiscount";
    public String discountUrl = "http://localhost:8081/catalog/products/{product_id}/price-checkout?quantity={quantity}";
    public String findByIdsUrl = "http://localhost:8081/catalog/products/byIds";
    public String productsWithDiscountUrl = "http://localhost:8081/catalog/products/volumePromotion";
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



    public List<Product> findProductsByIds(List<Long> ids){
        return List.of(Objects.requireNonNull(restClient.post()
                .uri(findByIdsUrl)
                .contentType(MediaType.APPLICATION_JSON)
                .body(ids)
                .retrieve()
                .onStatus(HttpStatusCode::isError, ((request, response) -> {
                    throw new ExternalMicroserviceException("CATALOG MICROSERVICE EXCEPTION: " + response.getStatusText()+" "+response.getBody());
                }))
                .body(Product[].class)));
    }


    public List<Product> getProductByIdWithDiscountedPrice(List<CartProductDto> cartProducts) {
        return List.of(Objects.requireNonNull(restClient.post()
                .uri(productsWithDiscountUrl)
                .contentType(MediaType.APPLICATION_JSON)
                .body(cartProducts)
                .retrieve()
                .onStatus(HttpStatusCode::isError, ((request, response) -> {
                    throw new ExternalMicroserviceException("CATALOG MICROSERVICE EXCEPTION: " + response.getStatusText()+" "+response.getBody());
                }))
                .body(Product[].class)));
    }
}
