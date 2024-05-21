package com.gftworkshop.cartMicroservice.services;

import com.gftworkshop.cartMicroservice.api.dto.User;
import com.gftworkshop.cartMicroservice.exceptions.ExternalMicroserviceException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

@Service
public class UserService {
    public String endpointUri = "http://localhost:8082/users/{id}";
    private final RestClient restClient;


    public UserService(RestClient restClient) {
        this.restClient = restClient;
    }

    public User getUserById(Long userId) {
        try {
            return restClient.get()
                    .uri(endpointUri, userId)
                    .retrieve()
                    .body(User.class);
        } catch (RestClientException e) {
            throw new ExternalMicroserviceException("USER MICROSERVICE EXCEPTION: " + e.getMessage());
        }
    }
}
