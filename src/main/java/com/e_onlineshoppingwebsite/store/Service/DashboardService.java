package com.e_onlineshoppingwebsite.store.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import com.e_onlineshoppingwebsite.store.Model.Product;

public interface DashboardService {

    long getTotalCustomers();

    long getTotalProducts();

    long getTotalOrders();

    long getPendingOrders();

    long getCompletedOrders();

    long getCancelledOrders();

    BigDecimal getTotalRevenue();

    BigDecimal getRevenueByDateRange(
            LocalDateTime startDate,
            LocalDateTime endDate);

    Map<String, BigDecimal> getMonthlyRevenue(int year);

    List<Product> getBestSellingProducts(int limit);

    List<Product> getLowStockProducts(int threshold);
}