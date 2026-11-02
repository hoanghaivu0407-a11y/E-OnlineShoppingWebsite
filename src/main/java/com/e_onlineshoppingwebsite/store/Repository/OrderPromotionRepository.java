package com.e_onlineshoppingwebsite.store.Repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.e_onlineshoppingwebsite.store.Model.OrderPromotion;

@Repository
public interface OrderPromotionRepository
                extends JpaRepository<OrderPromotion, Long> {

        List<OrderPromotion> findByOrder_OrderId(Long orderId);

        long countByPromotion_PromotionId(Long promotionId);

        boolean existsByOrder_OrderIdAndPromotion_PromotionId(
                        Long orderId,
                        Long promotionId);
}