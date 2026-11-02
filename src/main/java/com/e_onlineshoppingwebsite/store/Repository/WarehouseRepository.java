package com.e_onlineshoppingwebsite.store.Repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.e_onlineshoppingwebsite.store.Model.Warehouse;

@Repository
public interface WarehouseRepository extends JpaRepository<Warehouse, Long> {

    List<Warehouse> findByStatus(String status);

    List<Warehouse> findByWarehouseNameContainingIgnoreCase(
            String keyword);
}