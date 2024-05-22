package com.gftworkshop.cartMicroservice.services;

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
public class UserService {
    public String endpointUri = "http://localhost:8082/users/{id}";
    private final RestClient restClient;


    public UserService(RestClient restClient) {
        this.restClient = restClient;
    }

    public User getUserById(Long userId) {
        return restClient.get()
                .uri(endpointUri, userId)
                .retrieve()
                .onStatus(HttpStatusCode::isError, (request, response) -> {
                    throw new ExternalMicroserviceException("USER MICROSERVICE EXCEPTION: " + response.getStatusText()+" "+response.getBody());
                })
                .body(User.class);
    }
}
