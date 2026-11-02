
package com.e_onlineshoppingwebsite.store.Service.ServiceImpl;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.e_onlineshoppingwebsite.store.Model.Inventory;
import com.e_onlineshoppingwebsite.store.Model.Product;
import com.e_onlineshoppingwebsite.store.Model.Warehouse;
import com.e_onlineshoppingwebsite.store.Repository.InventoryRepository;
import com.e_onlineshoppingwebsite.store.Repository.ProductRepository;
import com.e_onlineshoppingwebsite.store.Repository.WarehouseRepository;
import com.e_onlineshoppingwebsite.store.Service.InventoriesService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class InventoriesServiceImpl implements InventoriesService {

    private final InventoryRepository inventoryRepository;
    private final ProductRepository productRepository;
    private final WarehouseRepository warehouseRepository;

    @Override
    public List<Inventory> getAllInventories() {
        return inventoryRepository.findAll();
    }

    @Override
    public Inventory getInventoryById(Long id) {
        return inventoryRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy tồn kho"));
    }

    @Override
    public List<Inventory> getInventoryByWarehouse(Long warehouseId) {
        return inventoryRepository.findByWarehouse_WarehouseId(warehouseId);
    }

    @Override
    public List<Inventory> getInventoryByProduct(Long productId) {
        return inventoryRepository.findByProduct_ProductId(productId);
    }

    @Override
    public int getAvailableQuantity(
            Long warehouseId, Long productId) {

        Inventory inventory = findInventory(warehouseId, productId);

        return inventory.getQuantity() - inventory.getReservedQuantity();
    }

    @Override
    public boolean checkStock(Long productId, int quantity) {

        if (quantity <= 0) {
            return false;
        }

        Long available = inventoryRepository
                .getTotalAvailableStock(productId);

        return available != null && available >= quantity;
    }

    @Override
    public void addStock(
            Long warehouseId, Long productId, int quantity) {

        validateQuantity(quantity);

        Inventory inventory = inventoryRepository
                .findByWarehouse_WarehouseIdAndProduct_ProductId(
                        warehouseId, productId)
                .orElse(null);

        if (inventory == null) {

            Warehouse warehouse = warehouseRepository.findById(warehouseId)
                    .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy kho"));

            Product product = productRepository.findById(productId)
                    .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy sản phẩm"));

            inventory = new Inventory();
            inventory.setWarehouse(warehouse);
            inventory.setProduct(product);
            inventory.setQuantity(quantity);
            inventory.setReservedQuantity(0);

        } else {
            inventory.setQuantity(
                    Math.addExact(inventory.getQuantity(), quantity));
        }

        inventoryRepository.save(inventory);
    }

    @Override
    public void reduceStock(
            Long warehouseId, Long productId, int quantity) {

        validateQuantity(quantity);

        Inventory inventory = findInventory(warehouseId, productId);

        int available = inventory.getQuantity() - inventory.getReservedQuantity();

        if (available < quantity) {
            throw new IllegalArgumentException(
                    "Không đủ hàng chưa được giữ chỗ để xuất kho");
        }

        inventory.setQuantity(inventory.getQuantity() - quantity);

        inventoryRepository.save(inventory);
    }

    @Override
    public void reserveStock(
            Long warehouseId, Long productId, int quantity) {

        validateQuantity(quantity);

        Inventory inventory = findInventory(warehouseId, productId);

        int available = inventory.getQuantity() - inventory.getReservedQuantity();

        if (available < quantity) {
            throw new IllegalArgumentException("Không đủ hàng để giữ chỗ");
        }

        inventory.setReservedQuantity(
                Math.addExact(inventory.getReservedQuantity(), quantity));

        inventoryRepository.save(inventory);
    }

    @Override
    public void releaseReservedStock(
            Long warehouseId, Long productId, int quantity) {

        validateQuantity(quantity);

        Inventory inventory = findInventory(warehouseId, productId);

        if (inventory.getReservedQuantity() < quantity) {
            throw new IllegalArgumentException(
                    "Số lượng giải phóng vượt quá số đã giữ");
        }

        inventory.setReservedQuantity(
                inventory.getReservedQuantity() - quantity);

        inventoryRepository.save(inventory);
    }

    @Override
    public List<Inventory> getLowStockProducts(int threshold) {

        if (threshold < 0) {
            throw new IllegalArgumentException("Ngưỡng tồn kho không hợp lệ");
        }

        return inventoryRepository.findAll()
                .stream()
                .filter(i -> i.getQuantity() - i.getReservedQuantity() <= threshold)
                .toList();
    }

    @Override
    public List<Inventory> getOutOfStockProducts() {

        return inventoryRepository.findAll()
                .stream()
                .filter(i -> i.getQuantity() - i.getReservedQuantity() <= 0)
                .toList();
    }

    @Override
    public int getTotalStockByProduct(Long productId) {

        Long available = inventoryRepository.getTotalAvailableStock(productId);

        return Math.toIntExact(available == null ? 0L : available);
    }

    private Inventory findInventory(
            Long warehouseId, Long productId) {

        return inventoryRepository
                .findByWarehouse_WarehouseIdAndProduct_ProductId(
                        warehouseId, productId)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Không tìm thấy sản phẩm trong kho"));
    }

    private void validateQuantity(int quantity) {

        if (quantity <= 0) {
            throw new IllegalArgumentException(
                    "Số lượng phải lớn hơn 0");
        }
    }
}
