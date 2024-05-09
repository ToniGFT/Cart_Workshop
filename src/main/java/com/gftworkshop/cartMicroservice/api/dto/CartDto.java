package com.gftworkshop.cartMicroservice.api.dto;

import com.gftworkshop.cartMicroservice.model.CartProduct;

import java.util.Date;
import java.util.List;


public class CartDto {
    private Long id;
    private Long user_id;
    private Date updated_at;
    private List<CartProduct> cartProducts;

    public CartDto() {
    }

    public CartDto(Long id, Long user_id, Date updated_at, List<CartProduct> cartProducts) {
        this.id = id;
        this.user_id = user_id;
        this.updated_at = updated_at;
        this.cartProducts = cartProducts;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUser_id() {
        return user_id;
    }

    public void setUser_id(Long user_id) {
        this.user_id = user_id;
    }

    public Date getUpdated_at() {
        return updated_at;
    }

    public void setUpdated_at(Date updated_at) {
        this.updated_at = updated_at;
    }

    public List<CartProduct> getCartProducts() {
        return cartProducts;
    }

    public void setCartProducts(List<CartProduct> cartProducts) {
        this.cartProducts = cartProducts;
    }
}
