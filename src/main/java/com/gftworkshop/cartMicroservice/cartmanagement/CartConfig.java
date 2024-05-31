package com.gftworkshop.cartMicroservice.cartmanagement;

import com.gftworkshop.cartMicroservice.repositories.CartProductRepository;
import com.gftworkshop.cartMicroservice.repositories.CartRepository;
import com.gftworkshop.cartMicroservice.services.ProductService;
import com.gftworkshop.cartMicroservice.services.UserService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CartConfig {

    @Bean
    public CartManager cartManager(CartRepository cartRepository, CartProductRepository cartProductRepository,
                                   ProductService productService, UserService userService, CartCalculator cartCalculator) {
        return new CartManager(cartRepository, cartProductRepository, productService, userService, cartCalculator);
    }

    @Bean
    public CartValidator cartValidator(ProductService productService, CartProductRepository cartProductRepository) {
        return new CartValidator(productService, cartProductRepository);
    }

    @Bean
    public CartCalculator cartCalculator(ProductService productService, UserService userService,
                                         CartRepository cartRepository) {
        return new CartCalculator(productService, userService, cartRepository);
    }

}

