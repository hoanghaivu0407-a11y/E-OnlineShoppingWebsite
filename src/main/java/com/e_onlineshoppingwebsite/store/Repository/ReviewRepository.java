package com.e_onlineshoppingwebsite.store.Repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.e_onlineshoppingwebsite.store.Model.Review;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {

        List<Review> findByProduct_ProductId(Long productId);

        List<Review> findByMember_UserId(Long userId);

        List<Review> findByProduct_ProductIdAndRating(
                        Long productId,
                        int rating);

        long countByProduct_ProductId(Long productId);

        @Query("""
                            SELECT AVG(r.rating)
                            FROM Review r
                            WHERE r.product.productId = :productId
                        """)
        Double calculateAverageRating(
                        @Param("productId") Long productId);
}