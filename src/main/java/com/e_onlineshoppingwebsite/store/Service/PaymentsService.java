package com.e_onlineshoppingwebsite.store.Service;

import java.util.List;

import com.e_onlineshoppingwebsite.store.Model.Payment;

public interface PaymentsService {

    List<Payment> getAllPayments();// lấy tất cả các thanh toán

    Payment getPaymentById(Long id);// lấy thanh toán theo id

    List<Payment> getPaymentsByOrderId(Long orderId);// lấy tất cả các thanh toán theo id đơn hàng

    List<Payment> findByStatus(String status);// lấy các thanh toán theo trạng thái

    List<Payment> findByPaymentMethod(String method);// lấy các thanh toán theo phương thức thanh toán

    Payment createPayment(
            Long orderId,
            String paymentMethod);

    void confirmPayment(Long paymentId);

    void failPayment(Long paymentId);

    void updatePaymentStatus(Long paymentId, String status);

    boolean isOrderPaid(Long orderId);
}