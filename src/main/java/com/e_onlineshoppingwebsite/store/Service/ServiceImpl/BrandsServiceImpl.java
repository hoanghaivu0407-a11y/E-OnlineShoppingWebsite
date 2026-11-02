
package com.e_onlineshoppingwebsite.store.Service.ServiceImpl;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.e_onlineshoppingwebsite.store.Model.Brand;
import com.e_onlineshoppingwebsite.store.Repository.BrandRepository;
import com.e_onlineshoppingwebsite.store.Service.BrandsService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class BrandsServiceImpl implements BrandsService {

    private final BrandRepository brandRepository;

    @Override
    public List<Brand> getAllBrands() {
        return brandRepository.findAll();
    }

    @Override
    public List<Brand> getActiveBrands() {
        return brandRepository.findByIsActiveTrue();
    }

    @Override
    public Brand getBrandById(Long id) {
        return brandRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy thương hiệu"));
    }

    @Override
    public List<Brand> searchByKeyword(String keyword) {
        return brandRepository.findByBrandNameContainingIgnoreCase(
                keyword == null ? "" : keyword);
    }

    @Override
    public boolean existsByBrandName(String brandName) {
        return brandRepository.existsByBrandName(brandName);
    }

    @Override
    public void save(Brand brand) {
        brandRepository.save(brand);
    }

    @Override
    public void update(Brand brand) {

        if (brand == null || brand.getBrandId() == null) {
            throw new IllegalArgumentException("Thiếu ID thương hiệu");
        }

        Brand old = getBrandById(brand.getBrandId());

        old.setBrandName(brand.getBrandName());
        old.setLogoUrl(brand.getLogoUrl());
        old.setDescription(brand.getDescription());
        old.setIsActive(brand.getIsActive());

        brandRepository.save(old);
    }

    @Override
    public void updateStatus(Long id, boolean active) {

        Brand brand = getBrandById(id);

        brand.setIsActive(active);

        brandRepository.save(brand);
    }

    @Override
    public void deleteBrand(Long id) {
        brandRepository.delete(getBrandById(id));
    }
}
