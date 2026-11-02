
package com.e_onlineshoppingwebsite.store.Service.ServiceImpl;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.e_onlineshoppingwebsite.store.Model.Order;
import com.e_onlineshoppingwebsite.store.Model.OrderPromotion;
import com.e_onlineshoppingwebsite.store.Model.Promotion;
import com.e_onlineshoppingwebsite.store.Repository.OrderPromotionRepository;
import com.e_onlineshoppingwebsite.store.Repository.OrderRepository;
import com.e_onlineshoppingwebsite.store.Repository.PromotionRepository;
import com.e_onlineshoppingwebsite.store.Service.PromotionsService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class PromotionsServiceImpl implements PromotionsService {

    private final PromotionRepository promotionRepository;
    private final OrderPromotionRepository orderPromotionRepository;
    private final OrderRepository orderRepository;

    @Override
    public List<Promotion> getAllPromotions() {
        return promotionRepository.findAll();
    }

    @Override
    public List<Promotion> getActivePromotions() {
        LocalDateTime now = LocalDateTime.now();
        return promotionRepository
                .findByIsActiveTrueAndStartDateLessThanEqualAndEndDateGreaterThanEqual(
                        now, now);
    }

    @Override
    public Promotion getPromotionById(Long id) {
        return promotionRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy khuyến mãi"));
    }

    @Override
    public Optional<Promotion> findByCode(String code) {
        return promotionRepository.findByCode(code);
    }

    @Override
    public boolean existsByCode(String code) {
        return promotionRepository.existsByCode(code);
    }

    @Override
    public boolean isValidPromotion(String code, BigDecimal orderTotal) {
        if (code == null || orderTotal == null
                || orderTotal.signum() < 0) {
            return false;
        }

        Optional<Promotion> found = promotionRepository.findByCode(code);
        if (found.isEmpty())
            return false;

        Promotion p = found.get();
        LocalDateTime now = LocalDateTime.now();

        if (!Boolean.TRUE.equals(p.getIsActive())
                || now.isBefore(p.getStartDate())
                || now.isAfter(p.getEndDate())) {
            return false;
        }

        if (orderTotal.compareTo(p.getMinOrderValue()) < 0) {
            return false;
        }

        return p.getUsageLimit() == null
                || orderPromotionRepository.countByPromotion_PromotionId(
                        p.getPromotionId()) < p.getUsageLimit();
    }

    @Override
    public BigDecimal calculateDiscount(
            String code, BigDecimal orderTotal) {

        if (!isValidPromotion(code, orderTotal)) {
            return BigDecimal.ZERO;
        }

        Promotion p = promotionRepository.findByCode(code).orElseThrow();
        BigDecimal discount;

        if ("percent".equalsIgnoreCase(p.getDiscountType())) {
            if (p.getDiscountValue().signum() < 0
                    || p.getDiscountValue().compareTo(
                            BigDecimal.valueOf(100)) > 0) {
                throw new IllegalArgumentException("Phần trăm giảm không hợp lệ");
            }

            discount = orderTotal.multiply(p.getDiscountValue())
                    .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
        } else if ("fixed".equalsIgnoreCase(p.getDiscountType())) {
            discount = p.getDiscountValue();
        } else {
            throw new IllegalArgumentException("Loại giảm giá không hợp lệ");
        }

        if (discount.signum() < 0) {
            throw new IllegalArgumentException("Giá trị giảm không hợp lệ");
        }

        if (p.getMaxDiscount() != null) {
            discount = discount.min(p.getMaxDiscount());
        }

        return discount.min(orderTotal);
    }

    @Override
    public void save(Promotion promotion) {
        if (promotionRepository.existsByCode(promotion.getCode())) {
            throw new IllegalArgumentException("Mã khuyến mãi đã tồn tại");
        }

        promotion.setPromotionId(null);
        promotionRepository.save(promotion);
    }

    @Override
    public void update(Promotion promotion) {
        Promotion old = getPromotionById(promotion.getPromotionId());

        if (!old.getCode().equals(promotion.getCode())
                && existsByCode(promotion.getCode())) {
            throw new IllegalArgumentException("Mã khuyến mãi đã tồn tại");
        }

        old.setCode(promotion.getCode());
        old.setPromotionName(promotion.getPromotionName());
        old.setDiscountType(promotion.getDiscountType());
        old.setDiscountValue(promotion.getDiscountValue());
        old.setMinOrderValue(promotion.getMinOrderValue());
        old.setMaxDiscount(promotion.getMaxDiscount());
        old.setUsageLimit(promotion.getUsageLimit());
        old.setStartDate(promotion.getStartDate());
        old.setEndDate(promotion.getEndDate());
        old.setIsActive(promotion.getIsActive());

        promotionRepository.save(old);
    }

    @Override
    public void updateStatus(Long id, boolean active) {
        Promotion p = getPromotionById(id);
        p.setIsActive(active);
        promotionRepository.save(p);
    }

    @Override
    public void deletePromotion(Long id) {
        Promotion p = getPromotionById(id);
        p.setIsActive(false);
        promotionRepository.save(p);
    }

    @Override
    public long countPromotionUsage(Long promotionId) {
        return orderPromotionRepository
                .countByPromotion_PromotionId(promotionId);
    }

    @Override
    public List<OrderPromotion> getPromotionsByOrderId(Long orderId) {
        return orderPromotionRepository.findByOrder_OrderId(orderId);
    }

    @Override
    public void applyPromotionToOrder(
            Long orderId, Long promotionId, BigDecimal discountAmount) {

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy đơn hàng"));

        Promotion promotion = getPromotionById(promotionId);

        if (!"pending".equals(order.getOrderStatus())) {
            throw new IllegalArgumentException("Đơn hàng không thể sửa");
        }

        if (orderPromotionRepository
                .existsByOrder_OrderIdAndPromotion_PromotionId(
                        orderId, promotionId)) {
            throw new IllegalArgumentException("Mã đã được áp dụng");
        }

        BigDecimal expected = calculateDiscount(
                promotion.getCode(), order.getSubtotal());

        if (discountAmount == null
                || discountAmount.compareTo(expected) != 0
                || expected.signum() <= 0) {
            throw new IllegalArgumentException("Số tiền giảm không hợp lệ");
        }

        OrderPromotion op = new OrderPromotion();
        op.setOrder(order);
        op.setPromotion(promotion);
        op.setDiscountAmount(expected);

        orderPromotionRepository.save(op);

        BigDecimal newDiscount = order.getDiscountAmount().add(expected);

        order.setDiscountAmount(newDiscount);
        order.setTotalAmount(
                order.getSubtotal().add(order.getShippingFee())
                        .subtract(newDiscount).max(BigDecimal.ZERO));

        orderRepository.save(order);
    }
}
