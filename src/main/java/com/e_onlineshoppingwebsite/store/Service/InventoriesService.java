package com.e_onlineshoppingwebsite.store.Service;

import java.util.List;

import com.e_onlineshoppingwebsite.store.Model.Inventory;

public interface InventoriesService {

    List<Inventory> getAllInventories();

    Inventory getInventoryById(Long id);

    List<Inventory> getInventoryByWarehouse(Long warehouseId);

    List<Inventory> getInventoryByProduct(Long productId);

    int getAvailableQuantity(
            Long warehouseId,
            Long productId);

    boolean checkStock(Long productId, int quantity);

    void addStock(
            Long warehouseId,
            Long productId,
            int quantity);

    void reduceStock(
            Long warehouseId,
            Long productId,
            int quantity);

    void reserveStock(
            Long warehouseId,
            Long productId,
            int quantity);

    void releaseReservedStock(
            Long warehouseId,
            Long productId,
            int quantity);

    List<Inventory> getLowStockProducts(int threshold);

    List<Inventory> getOutOfStockProducts();

    int getTotalStockByProduct(Long productId);
}