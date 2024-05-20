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

    private final RestClient restClient;

    @Autowired
    public UserService(RestClient restClient) {
        this.restClient = restClient;
    }

    public User getUserById(Long userId) {
        try {
            return restClient.get()
                    .uri("/users/{id}", userId)
                    .retrieve()
                    .body(User.class);
        }  catch (HttpClientErrorException e) {
            throw new ExternalMicroserviceException(e.getStatusCode(),"User Microservice Error: Client error occurred. \n"+ e.getMessage());
        } catch (HttpServerErrorException e) {
            throw new ExternalMicroserviceException(e.getStatusCode(),"User Microservice Error: server error occurred. \n"+ e.getMessage());
        } catch (RestClientException e) {
            throw new ExternalMicroserviceException("User Microservice Error: RestClient error occurred. \n"+ e.getMessage());
        } catch (Exception e) {
            throw new ExternalMicroserviceException("User Microservice Error: Unexpected error occurred. \n"+ e.getMessage());
        }
    }
}
