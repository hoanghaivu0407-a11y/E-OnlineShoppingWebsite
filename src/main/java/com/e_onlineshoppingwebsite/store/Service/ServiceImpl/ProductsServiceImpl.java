
package com.e_onlineshoppingwebsite.store.Service.ServiceImpl;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.e_onlineshoppingwebsite.store.Model.Product;
import com.e_onlineshoppingwebsite.store.Repository.ProductRepository;
import com.e_onlineshoppingwebsite.store.Service.ProductsService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class ProductsServiceImpl implements ProductsService {

    private final ProductRepository productRepository;

    @Override
    @Transactional(readOnly = true)
    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Product> getActiveProducts() {
        return productRepository.findByStatus("active");
    }

    @Override
    @Transactional(readOnly = true)
    public Product getProductById(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy sản phẩm"));
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Product> findBySku(String sku) {
        return productRepository.findBySku(sku);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Product> searchByKeyword(String keyword) {
        return productRepository.findByProductNameContainingIgnoreCase(
                keyword == null ? "" : keyword);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Product> getByCategory(Long categoryId) {
        return productRepository.findByCategory_CategoryId(categoryId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Product> getByBrand(Long brandId) {
        return productRepository.findByBrand_BrandId(brandId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Product> findByPriceRange(
            BigDecimal minPrice, BigDecimal maxPrice) {

        if (minPrice == null || maxPrice == null
                || minPrice.signum() < 0
                || minPrice.compareTo(maxPrice) > 0) {
            throw new IllegalArgumentException("Khoảng giá không hợp lệ");
        }

        return productRepository.findByPriceBetween(minPrice, maxPrice);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Product> findByStatus(String status) {
        return productRepository.findByStatus(status);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Product> getNewestProducts() {
        return productRepository.findTop10ByOrderByCreatedAtDesc();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Product> getSaleProducts() {
        return productRepository.findBySalePriceIsNotNull();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Product> getProductsWithPagination(int page, int size) {

        if (page < 0 || size <= 0) {
            throw new IllegalArgumentException("Phân trang không hợp lệ");
        }

        return productRepository.findAll(PageRequest.of(page, size))
                .getContent();
    }

    @Override
    @Transactional(readOnly = true)
    public long countProducts(String keyword) {
        if (keyword == null || keyword.isBlank()) {
            return productRepository.count();
        }

        return productRepository
                .findByProductNameContainingIgnoreCase(keyword)
                .size();
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsBySku(String sku) {
        return productRepository.existsBySku(sku);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsBySlug(String slug) {
        return productRepository.existsBySlug(slug);
    }

    @Override
    public void save(Product product) {

        if (product == null) {
            throw new IllegalArgumentException("Sản phẩm không hợp lệ");
        }

        if (productRepository.existsBySku(product.getSku())) {
            throw new IllegalArgumentException("SKU đã tồn tại");
        }

        if (productRepository.existsBySlug(product.getSlug())) {
            throw new IllegalArgumentException("Slug đã tồn tại");
        }

        product.setProductId(null);
        productRepository.save(product);
    }

    @Override
    public void update(Product product) {

        if (product == null || product.getProductId() == null) {
            throw new IllegalArgumentException("Thiếu ID sản phẩm");
        }

        Product old = getProductById(product.getProductId());

        productRepository.findBySku(product.getSku())
                .filter(p -> !p.getProductId().equals(old.getProductId()))
                .ifPresent(p -> {
                    throw new IllegalArgumentException("SKU đã tồn tại");
                });

        if (!old.getSlug().equals(product.getSlug())
                && productRepository.existsBySlug(product.getSlug())) {
            throw new IllegalArgumentException("Slug đã tồn tại");
        }

        old.setProductName(product.getProductName());
        old.setCategory(product.getCategory());
        old.setBrand(product.getBrand());
        old.setSlug(product.getSlug());
        old.setSku(product.getSku());
        old.setBarcode(product.getBarcode());
        old.setDescription(product.getDescription());
        old.setPrice(product.getPrice());
        old.setSalePrice(product.getSalePrice());
        old.setUnit(product.getUnit());
        old.setWeight(product.getWeight());
        old.setOrigin(product.getOrigin());
        old.setExpiryDate(product.getExpiryDate());
        old.setStatus(product.getStatus());

        productRepository.save(old);
    }

    @Override
    public void updatePrice(Long id, BigDecimal price) {

        if (price == null || price.signum() < 0) {
            throw new IllegalArgumentException("Giá không hợp lệ");
        }

        Product product = getProductById(id);

        if (product.getSalePrice() != null
                && product.getSalePrice().compareTo(price) > 0) {
            throw new IllegalArgumentException(
                    "Giá bán không được thấp hơn giá khuyến mãi");
        }

        product.setPrice(price);
        productRepository.save(product);
    }

    @Override
    public void updateSalePrice(Long id, BigDecimal salePrice) {

        Product product = getProductById(id);

        if (salePrice != null
                && (salePrice.signum() < 0
                        || salePrice.compareTo(product.getPrice()) > 0)) {
            throw new IllegalArgumentException(
                    "Giá khuyến mãi không hợp lệ");
        }

        product.setSalePrice(salePrice);
        productRepository.save(product);
    }

    @Override
    public void updateStatus(Long id, String status) {
        Product product = getProductById(id);
        product.setStatus(status);
        productRepository.save(product);
    }

    @Override
    public void deleteProduct(Long id) {
        Product product = getProductById(id);

        // Ẩn sản phẩm thay vì xóa dữ liệu đã liên kết đơn hàng.
        product.setStatus("inactive");
        productRepository.save(product);
    }
}
