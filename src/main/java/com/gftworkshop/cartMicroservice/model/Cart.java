package com.gftworkshop.cartMicroservice.model;


<<<<<<< HEAD
import com.gftworkshop.cartMicroservice.api.dto.CartDto;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
=======
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.gftworkshop.cartMicroservice.api.dto.CartDto;
import jakarta.persistence.*;
import lombok.*;
>>>>>>> main

import java.util.Date;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Builder
@Table(name = "cart")
public class Cart {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id")
    private Long user_id;

    @Column(name = "updated_at")
    private Date updated_at;

<<<<<<< HEAD
    @OneToMany(mappedBy = "cart")
=======
    @OneToMany(mappedBy = "cart", cascade = CascadeType.ALL)
    @JsonManagedReference
>>>>>>> main
    private List<CartProduct> cartProducts;
}
