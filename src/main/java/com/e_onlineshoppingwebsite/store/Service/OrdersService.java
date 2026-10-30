package com.e_onlineshoppingwebsite.store.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import com.e_onlineshoppingwebsite.store.Model.Order;
import com.e_onlineshoppingwebsite.store.Model.OrderItem;

public interface OrdersService {

    List<Order> getAllOrders();

    Order getOrderById(Long id);

    Order getOrderByCode(String orderCode);

    List<Order> getOrdersByUserId(Long userId);

    List<OrderItem> getOrderItems(Long orderId);

    List<Order> searchByKeyword(String keyword);

    List<Order> findByStatus(String status);

    List<Order> findByDateRange(
            LocalDateTime startDate,
            LocalDateTime endDate);

    List<Order> getOrdersWithPagination(
            int page, int size);

    long countOrders(String status);

    Order placeOrder(
            Long userId,
            Long addressId,
            String promotionCode);

    void cancelOrder(Long orderId, Long userId);

    void confirmOrder(Long orderId);

    void updateOrderStatus(Long orderId, String status);

    BigDecimal calculateOrderTotal(Long orderId);

    BigDecimal calculateRevenue(
            LocalDateTime startDate,
            LocalDateTime endDate);
}