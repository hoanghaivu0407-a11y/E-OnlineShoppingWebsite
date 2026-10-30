package com.e_onlineshoppingwebsite.store.Service;

import java.util.List;

import com.e_onlineshoppingwebsite.store.Model.Warehouse;

public interface WarehousesService {

    List<Warehouse> getAllWarehouses();

    Warehouse getWarehouseById(Long id);

    List<Warehouse> searchByKeyword(String keyword);

    List<Warehouse> findByStatus(String status);

    void save(Warehouse warehouse);

    void update(Warehouse warehouse);

    void updateStatus(Long id, String status);

    void deleteWarehouse(Long id);

    long countWarehouses();
}