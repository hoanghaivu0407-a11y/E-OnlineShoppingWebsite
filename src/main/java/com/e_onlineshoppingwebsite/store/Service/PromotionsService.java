package com.e_onlineshoppingwebsite.store.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import com.e_onlineshoppingwebsite.store.Model.OrderPromotion;
import com.e_onlineshoppingwebsite.store.Model.Promotion;

public interface PromotionsService {

    List<Promotion> getAllPromotions();

    List<Promotion> getActivePromotions();

    Promotion getPromotionById(Long id);

    Optional<Promotion> findByCode(String code);

    boolean existsByCode(String code);

    boolean isValidPromotion(
            String code,
            BigDecimal orderTotal);

    BigDecimal calculateDiscount(
            String code,
            BigDecimal orderTotal);

    void save(Promotion promotion);

    void update(Promotion promotion);

    void updateStatus(Long id, boolean active);

    void deletePromotion(Long id);

    long countPromotionUsage(Long promotionId);

    List<OrderPromotion> getPromotionsByOrderId(Long orderId);

    void applyPromotionToOrder(
            Long orderId,
            Long promotionId,
            BigDecimal discountAmount);
}