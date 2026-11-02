package com.e_onlineshoppingwebsite.store.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.e_onlineshoppingwebsite.store.Model.Product;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

        Optional<Product> findBySku(String sku);

        boolean existsBySku(String sku);

        boolean existsBySlug(String slug);

        List<Product> findByStatus(String status);

        List<Product> findByCategory_CategoryId(Long categoryId);

        List<Product> findByBrand_BrandId(Long brandId);

        List<Product> findByProductNameContainingIgnoreCase(String keyword);

        List<Product> findByPriceBetween(
                        BigDecimal minPrice,
                        BigDecimal maxPrice);

        List<Product> findBySalePriceIsNotNull();

        List<Product> findTop10ByOrderByCreatedAtDesc();

        Page<Product> findByStatus(String status, Pageable pageable);

        @Query("""
                            SELECT p FROM Product p
                            WHERE LOWER(p.productName)
                            LIKE LOWER(CONCAT('%', :keyword, '%'))
                        """)
        Page<Product> searchWithPagination(
                        @Param("keyword") String keyword,
                        Pageable pageable);

        long countByStatus(String status);
}