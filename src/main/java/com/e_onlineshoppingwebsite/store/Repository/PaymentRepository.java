package com.e_onlineshoppingwebsite.store.Repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.e_onlineshoppingwebsite.store.Model.Payment;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {

    List<Payment> findByOrder_OrderId(Long orderId);

    List<Payment> findByPaymentStatus(String status);

    List<Payment> findByPaymentMethod(String method);

    Optional<Payment> findByTransactionCode(String transactionCode);

    boolean existsByOrder_OrderIdAndPaymentStatus(
            Long orderId,
            String status);
}