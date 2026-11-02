package com.e_onlineshoppingwebsite.store.Repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.e_onlineshoppingwebsite.store.Model.Category;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {

    List<Category> findByIsActiveTrue();

    List<Category> findByParentIsNull();

    List<Category> findByParent_CategoryId(Long parentId);

    List<Category> findByCategoryNameContainingIgnoreCase(String keyword);

    boolean existsBySlug(String slug);
}