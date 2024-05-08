package com.gftworkshop.cartMicroservice.model;


import com.gftworkshop.cartMicroservice.api.dto.CartDto;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
@Entity
@Builder
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

    public Cart() {}

//    public static CartDto fromEntity(Cart entity){
//        return Cart.builder()
//                .id()
//    }
}
