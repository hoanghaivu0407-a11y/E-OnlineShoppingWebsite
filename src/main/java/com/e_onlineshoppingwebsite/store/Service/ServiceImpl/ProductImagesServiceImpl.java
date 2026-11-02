
package com.e_onlineshoppingwebsite.store.Service.ServiceImpl;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.e_onlineshoppingwebsite.store.Model.ProductImage;
import com.e_onlineshoppingwebsite.store.Repository.ProductImageRepository;
import com.e_onlineshoppingwebsite.store.Service.ProductImagesService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class ProductImagesServiceImpl implements ProductImagesService {

    private final ProductImageRepository productImageRepository;

    @Override
    @Transactional(readOnly = true)
    public List<ProductImage> getAllImages() {
        return productImageRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public ProductImage getImageById(Long id) {
        return productImageRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy hình ảnh"));
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductImage> getImagesByProductId(Long productId) {
        return productImageRepository
                .findByProduct_ProductIdOrderBySortOrderAsc(productId);
    }

    @Override
    @Transactional(readOnly = true)
    public ProductImage getPrimaryImage(Long productId) {
        return productImageRepository
                .findByProduct_ProductIdAndIsPrimaryTrue(productId)
                .orElse(null);
    }

    @Override
    public void save(ProductImage image) {
        if (image == null || image.getProduct() == null) {
            throw new IllegalArgumentException("Thông tin ảnh không hợp lệ");
        }

        if (Boolean.TRUE.equals(image.getIsPrimary())) {
            clearPrimaryImages(image.getProduct().getProductId());
        }

        image.setImageId(null);
        productImageRepository.save(image);
    }

    @Override
    public void update(ProductImage image) {
        if (image == null || image.getImageId() == null) {
            throw new IllegalArgumentException("Thiếu ID hình ảnh");
        }

        ProductImage old = getImageById(image.getImageId());

        old.setImageUrl(image.getImageUrl());
        old.setSortOrder(image.getSortOrder());

        if (Boolean.TRUE.equals(image.getIsPrimary())) {
            clearPrimaryImages(old.getProduct().getProductId());
            old.setIsPrimary(true);
        } else {
            old.setIsPrimary(false);
        }

        productImageRepository.save(old);
    }

    @Override
    public void deleteImage(Long imageId) {
        productImageRepository.delete(getImageById(imageId));
    }

    @Override
    public void setPrimaryImage(Long productId, Long imageId) {

        ProductImage selected = getImageById(imageId);

        if (!selected.getProduct().getProductId().equals(productId)) {
            throw new IllegalArgumentException(
                    "Hình ảnh không thuộc sản phẩm");
        }

        clearPrimaryImages(productId);

        selected.setIsPrimary(true);
        productImageRepository.save(selected);
    }

    @Override
    public void updateSortOrder(Long imageId, int sortOrder) {

        if (sortOrder < 0) {
            throw new IllegalArgumentException(
                    "Thứ tự hình ảnh không hợp lệ");
        }

        ProductImage image = getImageById(imageId);
        image.setSortOrder(sortOrder);

        productImageRepository.save(image);
    }

    private void clearPrimaryImages(Long productId) {

        List<ProductImage> images = productImageRepository
                .findByProduct_ProductIdOrderBySortOrderAsc(productId);

        for (ProductImage image : images) {
            image.setIsPrimary(false);
        }

        productImageRepository.saveAll(images);
    }
}
