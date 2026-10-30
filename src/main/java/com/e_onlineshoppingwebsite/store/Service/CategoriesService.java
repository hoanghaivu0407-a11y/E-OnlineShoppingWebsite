package com.e_onlineshoppingwebsite.store.Service;

import java.util.List;

import com.e_onlineshoppingwebsite.store.Model.Category;

public interface CategoriesService {

    List<Category> getAllCategories();

    List<Category> getActiveCategories();

    Category getCategoryById(Long id);

    List<Category> searchByKeyword(String keyword);

    List<Category> getParentCategories();

    List<Category> getSubCategories(Long parentId);

    boolean existsBySlug(String slug);

    void save(Category category);

    void update(Category category);

    void updateStatus(Long id, boolean active);

    void deleteCategory(Long id);
}