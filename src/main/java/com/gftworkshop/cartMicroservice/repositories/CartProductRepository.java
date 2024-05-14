package com.gftworkshop.cartMicroservice.repositories;

import com.gftworkshop.cartMicroservice.model.CartProduct;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface CartProductRepository extends JpaRepository<CartProduct, Long> {
    @Query("UPDATE CartProduct cp SET cp.quantity = :quantity WHERE cp.id = :id")
    int updateProductQuantity(@Param("id") Long id, @Param("quantity") int quantity);

    void removeAllByCartId(Long cartId);
}
