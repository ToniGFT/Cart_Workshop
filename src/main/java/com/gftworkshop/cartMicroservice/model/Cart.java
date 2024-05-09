package com.gftworkshop.cartMicroservice.model;


import jakarta.persistence.*;


import java.util.Date;
import java.util.List;


@Entity
@Table(name = "cart")
public class Cart {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name="user_id")
    private Long user_id;

    @Column(name="updated_at")
    private Date updated_at;

    @OneToMany(mappedBy = "cart")
    private List<CartProduct> cartProducts;

    public Cart() {
    }

    public Cart(Long id, Long user_id, Date updated_at, List<CartProduct> cartProducts) {
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
