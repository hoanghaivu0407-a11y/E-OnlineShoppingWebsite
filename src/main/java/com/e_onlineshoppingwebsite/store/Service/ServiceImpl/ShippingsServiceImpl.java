
package com.e_onlineshoppingwebsite.store.Service.ServiceImpl;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.e_onlineshoppingwebsite.store.Model.Order;
import com.e_onlineshoppingwebsite.store.Model.Shipping;
import com.e_onlineshoppingwebsite.store.Repository.OrderRepository;
import com.e_onlineshoppingwebsite.store.Repository.ShippingRepository;
import com.e_onlineshoppingwebsite.store.Service.ShippingsService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class ShippingsServiceImpl implements ShippingsService {

    private final ShippingRepository shippingRepository;
    private final OrderRepository orderRepository;

    @Override
    public List<Shipping> getAllShippings() {
        return shippingRepository.findAll();
    }

    @Override
    public Shipping getShippingById(Long id) {
        return shippingRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy vận chuyển"));
    }

    @Override
    public Shipping getShippingByOrderId(Long orderId) {
        return shippingRepository
                .findFirstByOrder_OrderIdOrderByCreatedAtDesc(orderId)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Đơn hàng chưa có thông tin vận chuyển"));
    }

    @Override
    public Shipping findByTrackingCode(String trackingCode) {
        return shippingRepository.findByTrackingCode(trackingCode)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Không tìm thấy mã vận đơn"));
    }

    @Override
    public List<Shipping> findByStatus(String status) {
        return shippingRepository.findByShippingStatus(status);
    }

    @Override
    public Shipping createShipping(
            Long orderId, String shippingProvider) {

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy đơn hàng"));

        if (!"confirmed".equals(order.getOrderStatus())) {
            throw new IllegalArgumentException(
                    "Đơn hàng chưa được xác nhận");
        }

        if (shippingProvider == null || shippingProvider.isBlank()) {
            throw new IllegalArgumentException(
                    "Đơn vị vận chuyển không hợp lệ");
        }

        Shipping shipping = new Shipping();
        shipping.setOrder(order);
        shipping.setShippingProvider(shippingProvider);
        shipping.setShippingStatus("pending");

        return shippingRepository.save(shipping);
    }

    @Override
    public void updateTrackingCode(
            Long shippingId, String trackingCode) {

        if (trackingCode == null || trackingCode.isBlank()) {
            throw new IllegalArgumentException("Mã vận đơn không hợp lệ");
        }

        Shipping shipping = getShippingById(shippingId);
        shipping.setTrackingCode(trackingCode);

        shippingRepository.save(shipping);
    }

    @Override
    public void updateShippingStatus(Long shippingId, String status) {

        if (status == null || status.isBlank()) {
            throw new IllegalArgumentException(
                    "Trạng thái vận chuyển không hợp lệ");
        }

        Shipping shipping = getShippingById(shippingId);
        shipping.setShippingStatus(status);

        shippingRepository.save(shipping);
    }

    @Override
    public void updateEstimatedDelivery(
            Long shippingId, LocalDateTime estimatedDelivery) {

        Shipping shipping = getShippingById(shippingId);
        shipping.setEstimatedDelivery(estimatedDelivery);

        shippingRepository.save(shipping);
    }

    @Override
    public void confirmDelivery(Long shippingId) {

        Shipping shipping = getShippingById(shippingId);

        if ("delivered".equals(shipping.getShippingStatus())) {
            throw new IllegalArgumentException(
                    "Đơn đã được xác nhận giao hàng");
        }

        shipping.setShippingStatus("delivered");
        shipping.setDeliveredAt(LocalDateTime.now());

        shippingRepository.save(shipping);

        Order order = shipping.getOrder();
        order.setOrderStatus("completed");

        orderRepository.save(order);
    }
}
