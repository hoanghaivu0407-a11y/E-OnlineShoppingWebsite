
package com.e_onlineshoppingwebsite.store.Service.ServiceImpl;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.e_onlineshoppingwebsite.store.Model.Warehouse;
import com.e_onlineshoppingwebsite.store.Repository.WarehouseRepository;
import com.e_onlineshoppingwebsite.store.Service.WarehousesService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class WarehousesServiceImpl implements WarehousesService {

    private final WarehouseRepository warehouseRepository;

    @Override
    public List<Warehouse> getAllWarehouses() {
        return warehouseRepository.findAll();
    }

    @Override
    public Warehouse getWarehouseById(Long id) {
        return warehouseRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy kho hàng"));
    }

    @Override
    public List<Warehouse> searchByKeyword(String keyword) {
        return warehouseRepository.findByWarehouseNameContainingIgnoreCase(
                keyword == null ? "" : keyword);
    }

    @Override
    public List<Warehouse> findByStatus(String status) {
        return warehouseRepository.findByStatus(status);
    }

    @Override
    public void save(Warehouse warehouse) {
        if (warehouse == null) {
            throw new IllegalArgumentException("Kho hàng không hợp lệ");
        }

        warehouse.setWarehouseId(null);
        warehouseRepository.save(warehouse);
    }

    @Override
    public void update(Warehouse warehouse) {
        if (warehouse == null || warehouse.getWarehouseId() == null) {
            throw new IllegalArgumentException("Thiếu ID kho hàng");
        }

        Warehouse old = getWarehouseById(warehouse.getWarehouseId());

        old.setWarehouseName(warehouse.getWarehouseName());
        old.setAddress(warehouse.getAddress());
        old.setPhone(warehouse.getPhone());
        old.setStatus(warehouse.getStatus());

        warehouseRepository.save(old);
    }

    @Override
    public void updateStatus(Long id, String status) {
        Warehouse warehouse = getWarehouseById(id);

        warehouse.setStatus(status);
        warehouseRepository.save(warehouse);
    }

    @Override
    public void deleteWarehouse(Long id) {
        Warehouse warehouse = getWarehouseById(id);

        // Ngừng hoạt động thay vì xóa kho có dữ liệu tồn kho.
        warehouse.setStatus("inactive");

        warehouseRepository.save(warehouse);
    }

    @Override
    public long countWarehouses() {
        return warehouseRepository.count();
    }
}
