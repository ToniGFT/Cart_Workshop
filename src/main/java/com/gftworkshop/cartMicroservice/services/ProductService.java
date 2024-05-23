package com.gftworkshop.cartMicroservice.services;

import com.gftworkshop.cartMicroservice.api.dto.Product;
import com.gftworkshop.cartMicroservice.api.dto.User;
import com.gftworkshop.cartMicroservice.exceptions.ExternalMicroserviceException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

@Service
public class ProductService {
    public String endpointUri = "http://localhost:8081/catalog/products/{id}";
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
}
