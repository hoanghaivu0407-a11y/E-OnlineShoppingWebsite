package com.e_onlineshoppingwebsite.store.Repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.e_onlineshoppingwebsite.store.Model.Inventory;

@Repository
public interface InventoryRepository extends JpaRepository<Inventory, Long> {

        List<Inventory> findByWarehouse_WarehouseId(Long warehouseId);

        List<Inventory> findByProduct_ProductId(Long productId);

        Optional<Inventory> findByWarehouse_WarehouseIdAndProduct_ProductId(
                        Long warehouseId,
                        Long productId);

        List<Inventory> findByQuantityLessThanEqual(int threshold);

        List<Inventory> findByQuantity(int quantity);

        @Query("""
                            SELECT COALESCE(SUM(i.quantity - i.reservedQuantity), 0)
                            FROM Inventory i
                            WHERE i.product.productId = :productId
                        """)
        Long getTotalAvailableStock(
                        @Param("productId") Long productId);
}