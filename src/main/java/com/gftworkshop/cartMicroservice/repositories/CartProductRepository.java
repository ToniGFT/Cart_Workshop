package com.gftworkshop.cartMicroservice.repositories;

import com.gftworkshop.cartMicroservice.model.CartProduct;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CartProductRepository extends JpaRepository<CartProduct, Long> {
}
