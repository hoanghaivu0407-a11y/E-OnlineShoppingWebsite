package com.e_onlineshoppingwebsite.store.Service;

import java.time.LocalDateTime;
import java.util.List;

import com.e_onlineshoppingwebsite.store.Model.Shipping;

public interface ShippingsService {

    List<Shipping> getAllShippings();

    Shipping getShippingById(Long id);

    Shipping getShippingByOrderId(Long orderId);

    Shipping findByTrackingCode(String trackingCode);

    List<Shipping> findByStatus(String status);

    Shipping createShipping(
            Long orderId,
            String shippingProvider);

    void updateTrackingCode(
            Long shippingId,
            String trackingCode);

    void updateShippingStatus(
            Long shippingId,
            String status);

    void updateEstimatedDelivery(
            Long shippingId,
            LocalDateTime estimatedDelivery);

    void confirmDelivery(Long shippingId);
}