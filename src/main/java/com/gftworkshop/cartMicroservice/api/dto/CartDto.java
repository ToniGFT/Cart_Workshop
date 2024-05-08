package com.gftworkshop.cartMicroservice.api.dto;


import java.util.List;

public class CartDto {
    private Long id;
    private List<CartProductDto> cartProducts;

    public CartDto() {}

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public List<CartProductDto> getCartProducts() {
        return cartProducts;
    }

    public void setCartProducts(List<CartProductDto> cartProducts) {
        this.cartProducts = cartProducts;
    }
}
