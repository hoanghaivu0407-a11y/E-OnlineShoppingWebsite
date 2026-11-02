package com.e_onlineshoppingwebsite.store.Repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.e_onlineshoppingwebsite.store.Model.ProductImage;

@Repository
public interface ProductImageRepository
                extends JpaRepository<ProductImage, Long> {

        List<ProductImage> findByProduct_ProductIdOrderBySortOrderAsc(
                        Long productId);

        Optional<ProductImage> findByProduct_ProductIdAndIsPrimaryTrue(
                        Long productId);

        long countByProduct_ProductId(Long productId);
}