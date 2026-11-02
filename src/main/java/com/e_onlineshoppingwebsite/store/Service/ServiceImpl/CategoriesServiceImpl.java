
package com.e_onlineshoppingwebsite.store.Service.ServiceImpl;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.e_onlineshoppingwebsite.store.Model.Category;
import com.e_onlineshoppingwebsite.store.Repository.CategoryRepository;
import com.e_onlineshoppingwebsite.store.Service.CategoriesService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class CategoriesServiceImpl implements CategoriesService {

    private final CategoryRepository categoryRepository;

    @Override
    public List<Category> getAllCategories() {
        return categoryRepository.findAll();
    }

    @Override
    public List<Category> getActiveCategories() {
        return categoryRepository.findByIsActiveTrue();
    }

    @Override
    public Category getCategoryById(Long id) {
        return categoryRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy danh mục"));
    }

    @Override
    public List<Category> searchByKeyword(String keyword) {
        return categoryRepository.findByCategoryNameContainingIgnoreCase(
                keyword == null ? "" : keyword);
    }

    @Override
    public List<Category> getParentCategories() {
        return categoryRepository.findByParentIsNull();
    }

    @Override
    public List<Category> getSubCategories(Long parentId) {
        return categoryRepository.findByParent_CategoryId(parentId);
    }

    @Override
    public boolean existsBySlug(String slug) {
        return categoryRepository.existsBySlug(slug);
    }

    @Override
    public void save(Category category) {
        categoryRepository.save(category);
    }

    @Override
    public void update(Category category) {

        if (category == null || category.getCategoryId() == null) {
            throw new IllegalArgumentException("Thiếu ID danh mục");
        }

        Category old = getCategoryById(category.getCategoryId());

        old.setCategoryName(category.getCategoryName());
        old.setSlug(category.getSlug());
        old.setParent(category.getParent());
        old.setImageUrl(category.getImageUrl());
        old.setDescription(category.getDescription());
        old.setIsActive(category.getIsActive());

        categoryRepository.save(old);
    }

    @Override
    public void updateStatus(Long id, boolean active) {

        Category category = getCategoryById(id);

        category.setIsActive(active);

        categoryRepository.save(category);
    }

    @Override
    public void deleteCategory(Long id) {
        categoryRepository.delete(getCategoryById(id));
    }
}
