package com.e_onlineshoppingwebsite.store.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.e_onlineshoppingwebsite.store.Model.Order;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {

        Optional<Order> findByOrderCode(String orderCode);

        List<Order> findByMember_UserId(Long userId);

        List<Order> findByOrderStatus(String status);

        List<Order> findByCreatedAtBetween(
                        LocalDateTime startDate,
                        LocalDateTime endDate);

        Page<Order> findByOrderStatus(
                        String status,
                        Pageable pageable);

        long countByOrderStatus(String status);

        @Query("""
                            SELECT o FROM Order o
                            WHERE LOWER(o.orderCode)
                            LIKE LOWER(CONCAT('%', :keyword, '%'))
                            OR LOWER(o.receiverName)
                            LIKE LOWER(CONCAT('%', :keyword, '%'))
                            OR o.receiverPhone LIKE CONCAT('%', :keyword, '%')
                        """)
        List<Order> searchByKeyword(@Param("keyword") String keyword);

        @Query("""
                            SELECT SUM(o.totalAmount)
                            FROM Order o
                            WHERE o.orderStatus = 'completed'
                            AND o.createdAt BETWEEN :startDate AND :endDate
                        """)
        java.math.BigDecimal calculateRevenue(
                        @Param("startDate") LocalDateTime startDate,
                        @Param("endDate") LocalDateTime endDate);
}