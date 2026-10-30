package com.e_onlineshoppingwebsite.store.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import com.e_onlineshoppingwebsite.store.Model.Product;

public interface ProductsService {

    List<Product> getAllProducts();

    List<Product> getActiveProducts();

    Product getProductById(Long id);

    Optional<Product> findBySku(String sku);

    List<Product> searchByKeyword(String keyword);

    List<Product> getByCategory(Long categoryId);

    List<Product> getByBrand(Long brandId);

    List<Product> findByPriceRange(BigDecimal minPrice, BigDecimal maxPrice);

    List<Product> findByStatus(String status);

    List<Product> getNewestProducts();

    List<Product> getSaleProducts();

    List<Product> getProductsWithPagination(
            int page, int size);

    long countProducts(String keyword);

    boolean existsBySku(String sku);

    boolean existsBySlug(String slug);

    void save(Product product);

    void update(Product product);

    void updatePrice(Long id, BigDecimal price);

    void updateSalePrice(Long id, BigDecimal salePrice);

    void updateStatus(Long id, String status);

    void deleteProduct(Long id);

}
