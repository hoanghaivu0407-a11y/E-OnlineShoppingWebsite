
package com.e_onlineshoppingwebsite.store.Service.ServiceImpl;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.e_onlineshoppingwebsite.store.Model.Order;
import com.e_onlineshoppingwebsite.store.Model.Payment;
import com.e_onlineshoppingwebsite.store.Repository.OrderRepository;
import com.e_onlineshoppingwebsite.store.Repository.PaymentRepository;
import com.e_onlineshoppingwebsite.store.Service.PaymentsService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class PaymentsServiceImpl implements PaymentsService {

    private final PaymentRepository paymentRepository;
    private final OrderRepository orderRepository;

    @Override
    public List<Payment> getAllPayments() {
        return paymentRepository.findAll();
    }

    @Override
    public Payment getPaymentById(Long id) {
        return paymentRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy thanh toán"));
    }

    @Override
    public List<Payment> getPaymentsByOrderId(Long orderId) {
        return paymentRepository.findByOrder_OrderId(orderId);
    }

    @Override
    public List<Payment> findByStatus(String status) {
        return paymentRepository.findByPaymentStatus(status);
    }

    @Override
    public List<Payment> findByPaymentMethod(String method) {
        return paymentRepository.findByPaymentMethod(method);
    }

    @Override
    public Payment createPayment(Long orderId, String paymentMethod) {

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy đơn hàng"));

        if ("cancelled".equals(order.getOrderStatus())) {
            throw new IllegalArgumentException("Đơn hàng đã bị hủy");
        }

        if (paymentMethod == null || paymentMethod.isBlank()) {
            throw new IllegalArgumentException(
                    "Phương thức thanh toán không hợp lệ");
        }

        if (isOrderPaid(orderId)) {
            throw new IllegalArgumentException(
                    "Đơn hàng đã được thanh toán");
        }

        Payment payment = new Payment();
        payment.setOrder(order);
        payment.setPaymentMethod(paymentMethod);
        payment.setAmount(order.getTotalAmount());
        payment.setPaymentStatus("pending");

        return paymentRepository.save(payment);
    }

    @Override
    public void confirmPayment(Long paymentId) {

        Payment payment = getPaymentById(paymentId);

        if (!"pending".equals(payment.getPaymentStatus())) {
            throw new IllegalArgumentException(
                    "Thanh toán không ở trạng thái chờ");
        }

        if (isOrderPaid(payment.getOrder().getOrderId())) {
            throw new IllegalArgumentException(
                    "Đơn hàng đã được thanh toán");
        }

        payment.setPaymentStatus("completed");
        payment.setPaidAt(LocalDateTime.now());

        paymentRepository.save(payment);
    }

    @Override
    public void failPayment(Long paymentId) {

        Payment payment = getPaymentById(paymentId);

        if (!"pending".equals(payment.getPaymentStatus())) {
            throw new IllegalArgumentException(
                    "Không thể đánh dấu thất bại cho giao dịch này");
        }

        payment.setPaymentStatus("failed");
        paymentRepository.save(payment);
    }

    @Override
    public void updatePaymentStatus(Long paymentId, String status) {

        if ("completed".equals(status)) {
            confirmPayment(paymentId);
        } else if ("failed".equals(status)) {
            failPayment(paymentId);
        } else {
            throw new IllegalArgumentException(
                    "Chỉ hỗ trợ xác nhận hoặc thất bại qua phương thức này");
        }
    }

    @Override
    public boolean isOrderPaid(Long orderId) {
        return paymentRepository
                .existsByOrder_OrderIdAndPaymentStatus(
                        orderId, "completed");
    }
}
