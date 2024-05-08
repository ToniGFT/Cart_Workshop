package com.gftworkshop.cartMicroservice.repositories;


import com.gftworkshop.cartMicroservice.model.Cart;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CartRepository extends JpaRepository<Cart, Long> {

}
