package com.e_onlineshoppingwebsite.store.Repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.e_onlineshoppingwebsite.store.Model.Brand;

@Repository
public interface BrandRepository extends JpaRepository<Brand, Long> {

    List<Brand> findByIsActiveTrue();

    List<Brand> findByBrandNameContainingIgnoreCase(String keyword);

    boolean existsByBrandName(String brandName);
}