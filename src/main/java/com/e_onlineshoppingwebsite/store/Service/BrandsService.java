package com.e_onlineshoppingwebsite.store.Service;

import java.util.List;

import com.e_onlineshoppingwebsite.store.Model.Brand;

public interface BrandsService {

    List<Brand> getAllBrands();

    List<Brand> getActiveBrands();

    Brand getBrandById(Long id);

    void save(Brand brand);

    void update(Brand brand);

    void updateStatus(Long id, boolean active);

    void deleteBrand(Long id);

    List<Brand> searchByKeyword(String keyword);

    boolean existsByBrandName(String brandName);
}