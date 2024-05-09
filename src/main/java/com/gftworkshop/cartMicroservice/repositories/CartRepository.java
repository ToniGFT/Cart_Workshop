package com.gftworkshop.cartMicroservice.repositories;


import com.gftworkshop.cartMicroservice.model.Cart;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CartRepository extends JpaRepository<Cart, Long> {
}
