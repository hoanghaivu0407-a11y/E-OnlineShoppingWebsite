package com.e_onlineshoppingwebsite.store.Service;

import java.util.List;
import com.e_onlineshoppingwebsite.store.Model.ProductImage;

public interface ProductImagesService {

    List<ProductImage> getAllImages();

    ProductImage getImageById(Long id);

    List<ProductImage> getImagesByProductId(Long productId);

    ProductImage getPrimaryImage(Long productId);

    void save(ProductImage image);

    void update(ProductImage image);

    void deleteImage(Long imageId);

    void setPrimaryImage(Long productId, Long imageId);

    void updateSortOrder(Long imageId, int sortOrder);
}