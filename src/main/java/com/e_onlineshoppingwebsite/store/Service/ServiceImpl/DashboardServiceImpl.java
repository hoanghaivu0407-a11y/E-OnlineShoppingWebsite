
package com.e_onlineshoppingwebsite.store.Service.ServiceImpl;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.e_onlineshoppingwebsite.store.Model.OrderItem;
import com.e_onlineshoppingwebsite.store.Model.Product;
import com.e_onlineshoppingwebsite.store.Repository.InventoryRepository;
import com.e_onlineshoppingwebsite.store.Repository.MemberRepository;
import com.e_onlineshoppingwebsite.store.Repository.OrderItemRepository;
import com.e_onlineshoppingwebsite.store.Repository.OrderRepository;
import com.e_onlineshoppingwebsite.store.Repository.ProductRepository;
import com.e_onlineshoppingwebsite.store.Service.DashboardService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DashboardServiceImpl implements DashboardService {

    private final MemberRepository memberRepository;
    private final ProductRepository productRepository;
    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final InventoryRepository inventoryRepository;

    @Override
    public long getTotalCustomers() {
        return memberRepository.countByRole_RoleName("customer");
    }

    @Override
    public long getTotalProducts() {
        return productRepository.count();
    }

    @Override
    public long getTotalOrders() {
        return orderRepository.count();
    }

    @Override
    public long getPendingOrders() {
        return orderRepository.countByOrderStatus("pending");
    }

    @Override
    public long getCompletedOrders() {
        return orderRepository.countByOrderStatus("completed");
    }

    @Override
    public long getCancelledOrders() {
        return orderRepository.countByOrderStatus("cancelled");
    }

    @Override
    public BigDecimal getTotalRevenue() {
        return getRevenueByDateRange(
                LocalDateTime.of(2000, 1, 1, 0, 0),
                LocalDateTime.now());
    }

    @Override
    public BigDecimal getRevenueByDateRange(
            LocalDateTime startDate, LocalDateTime endDate) {

        BigDecimal result = orderRepository.calculateRevenue(startDate, endDate);

        return result == null ? BigDecimal.ZERO : result;
    }

    @Override
    public Map<String, BigDecimal> getMonthlyRevenue(int year) {

        Map<String, BigDecimal> result = new LinkedHashMap<>();

        for (int month = 1; month <= 12; month++) {

            LocalDateTime start = LocalDateTime.of(year, month, 1, 0, 0);

            LocalDateTime end = start.plusMonths(1).minusNanos(1);

            result.put(String.format("%02d", month),
                    getRevenueByDateRange(start, end));
        }

        return result;
    }

    @Override
    public List<Product> getBestSellingProducts(int limit) {

        if (limit <= 0) {
            return List.of();
        }

        // Chỉ tính các dòng hàng thuộc đơn đã hoàn thành.
        Map<Long, Integer> sold = orderItemRepository.findAll()
                .stream()
                .filter(item -> "completed".equals(
                        item.getOrder().getOrderStatus()))
                .collect(Collectors.groupingBy(
                        item -> item.getProduct().getProductId(),
                        Collectors.summingInt(OrderItem::getQuantity)));

        return sold.entrySet().stream()
                .sorted(Map.Entry.<Long, Integer>comparingByValue(
                        Comparator.reverseOrder()))
                .limit(limit)
                .map(entry -> productRepository
                        .findById(entry.getKey()).orElse(null))
                .filter(p -> p != null)
                .toList();
    }

    @Override
    public List<Product> getLowStockProducts(int threshold) {

        if (threshold < 0) {
            throw new IllegalArgumentException("Ngưỡng tồn kho không hợp lệ");
        }

        return inventoryRepository.findAll()
                .stream()
                .filter(i -> i.getQuantity() - i.getReservedQuantity() <= threshold)
                .map(i -> i.getProduct())
                .collect(Collectors.toMap(
                        Product::getProductId,
                        p -> p,
                        (first, second) -> first,
                        LinkedHashMap::new))
                .values()
                .stream()
                .toList();
    }
}
