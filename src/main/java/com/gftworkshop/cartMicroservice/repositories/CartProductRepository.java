package com.gftworkshop.cartMicroservice.repositories;

import com.gftworkshop.cartMicroservice.model.CartProduct;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface CartProductRepository extends JpaRepository<CartProduct, Long> {

    @Query("UPDATE CartProduct cp SET cp.quantity = ?2 WHERE cp.id = ?1")
    int updateProductQuantity(Long productId, int newQuantity);
}
