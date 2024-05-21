package com.gftworkshop.cartMicroservice.services;

import com.gftworkshop.cartMicroservice.api.dto.Product;
import com.gftworkshop.cartMicroservice.exceptions.ExternalMicroserviceException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

@Service
public class ProductService {
    private final String endpointUri = "http://localhost:8081/catalog/products/{id}";
    private final RestClient restClient;

    @Autowired
    public ProductService(RestClient restClient) {
        this.restClient = restClient;
    }

    public Product getProductById(Long productId) {
        try {
            return restClient.get()
                    .uri(endpointUri, productId)
                    .retrieve()
                    .body(Product.class);
        } catch (HttpClientErrorException e) {
            throw new ExternalMicroserviceException(e.getStatusCode(),"Catalog Microservice Error: Client error occurred. \n"+ e.getMessage());
        } catch (HttpServerErrorException e) {
            throw new ExternalMicroserviceException(e.getStatusCode(),"Catalog Microservice Error: server error occurred. \n"+ e.getMessage());
        } catch (RestClientException e) {
            throw new ExternalMicroserviceException("Catalog Microservice Error: RestClient error occurred. \n"+ e.getMessage());
        } catch (Exception e) {
            throw new ExternalMicroserviceException("Catalog Microservice Error: Unexpected error occurred. \n"+ e.getMessage());
        }
    }
}
