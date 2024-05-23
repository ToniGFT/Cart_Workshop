package com.gftworkshop.cartMicroservice.services;

import com.gftworkshop.cartMicroservice.api.dto.Product;
import com.gftworkshop.cartMicroservice.exceptions.ExternalMicroserviceException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.web.client.RestClient;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

class ProductServiceTest {

    private RestClient restClient;
    private ProductService productService;

    @BeforeEach
    void setUp() {
        restClient = Mockito.mock(RestClient.class);
        productService = new ProductService(restClient);
    }

    @Nested
    @DisplayName("When getProductById")
    class WhenGetProductById {

        @Test
        @DisplayName("Given a valid ID then return Product")
        void getProductByIdWithValidId() {
            Product expectedProduct = Product.builder()
                    .id(1L)
                    .name("Sample Product")
                    .description("Description here")
                    .price(new BigDecimal("19.99"))
                    .current_stock(100)
                    .weight(1.5)
                    .build();
            RestClient.RequestHeadersUriSpec request = Mockito.mock(RestClient.RequestHeadersUriSpec.class);
            RestClient.ResponseSpec response = Mockito.mock(RestClient.ResponseSpec.class);

            when(restClient.get()).thenReturn(request);
            when(request.uri(any(String.class), anyLong())).thenReturn(request);
            when(request.retrieve()).thenReturn(response);
            when(response.onStatus(any(), any())).thenReturn(response);
            when(response.body(Product.class)).thenReturn(expectedProduct);

            Product actualProduct = productService.getProductById(1L);

            assertThat(actualProduct).isNotNull();
            assertThat(actualProduct).isEqualTo(expectedProduct);
        }

        @Test
        @DisplayName("Given an invalid ID then throw ExternalMicroserviceException")
        void getProductByIdWithInvalidId() {
            RestClient.RequestHeadersUriSpec request = Mockito.mock(RestClient.RequestHeadersUriSpec.class);
            RestClient.ResponseSpec response = Mockito.mock(RestClient.ResponseSpec.class);

            when(restClient.get()).thenReturn(request);
            when(request.uri(any(String.class), anyLong())).thenReturn(request);
            when(request.retrieve()).thenReturn(response);
            when(response.onStatus(any(), any())).thenThrow(new ExternalMicroserviceException("CATALOG MICROSERVICE EXCEPTION: Not Found"));

            assertThatThrownBy(() -> productService.getProductById(-1L))
                    .isInstanceOf(ExternalMicroserviceException.class)
                    .hasMessageContaining("CATALOG MICROSERVICE EXCEPTION: Not Found");
        }
    }

    @Nested
    @DisplayName("When getProductDiscountedPrice")
    class WhenGetProductDiscountedPrice {

        @Test
        @DisplayName("Given valid parameters then return discounted price")
        void getProductDiscountedPriceWithValidParameters() {
            float expectedPrice = 15.99f;
            RestClient.RequestHeadersUriSpec request = Mockito.mock(RestClient.RequestHeadersUriSpec.class);
            RestClient.ResponseSpec response = Mockito.mock(RestClient.ResponseSpec.class);

            when(restClient.get()).thenReturn(request);
            when(request.uri(any(String.class), anyLong(), anyInt())).thenReturn(request);
            when(request.retrieve()).thenReturn(response);
            when(response.onStatus(any(), any())).thenReturn(response);
            when(response.body(Float.class)).thenReturn(expectedPrice);

            float actualPrice = productService.getProductDiscountedPrice(1L, 2);

            assertThat(actualPrice).isEqualTo(expectedPrice);
        }

        @Test
        @DisplayName("Given invalid parameters then throw ExternalMicroserviceException")
        void getProductDiscountedPriceWithInvalidParameters() {
            RestClient.RequestHeadersUriSpec request = Mockito.mock(RestClient.RequestHeadersUriSpec.class);
            RestClient.ResponseSpec response = Mockito.mock(RestClient.ResponseSpec.class);

            when(restClient.get()).thenReturn(request);
            when(request.uri(any(String.class), anyLong(), anyInt())).thenReturn(request);
            when(request.retrieve()).thenReturn(response);
            when(response.onStatus(any(), any())).thenThrow(new ExternalMicroserviceException("CATALOG MICROSERVICE EXCEPTION: Error"));

            assertThatThrownBy(() -> productService.getProductDiscountedPrice(1L, -1))
                    .isInstanceOf(ExternalMicroserviceException.class)
                    .hasMessageContaining("CATALOG MICROSERVICE EXCEPTION: Error");
        }
    }
}
