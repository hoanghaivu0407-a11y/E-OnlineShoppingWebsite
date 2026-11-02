package com.e_onlineshoppingwebsite.store.Repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.e_onlineshoppingwebsite.store.Model.Shipping;

@Repository
public interface ShippingRepository extends JpaRepository<Shipping, Long> {

    Optional<Shipping> findFirstByOrder_OrderIdOrderByCreatedAtDesc(
            Long orderId);

    Optional<Shipping> findByTrackingCode(String trackingCode);

    List<Shipping> findByShippingStatus(String status);
}