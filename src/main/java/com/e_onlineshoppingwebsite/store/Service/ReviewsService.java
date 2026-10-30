package com.e_onlineshoppingwebsite.store.Service;

import java.util.List;

import com.e_onlineshoppingwebsite.store.Model.Review;

public interface ReviewsService {

    List<Review> getAllReviews();

    Review getReviewById(Long id);

    List<Review> getReviewsByProductId(Long productId);

    List<Review> getReviewsByUserId(Long userId);

    List<Review> findByRating(Long productId, int rating);

    double calculateAverageRating(Long productId);

    long countReviewsByProduct(Long productId);

    boolean canReviewProduct(Long userId, Long productId);

    void save(Review review);

    void update(Review review);

    void deleteReview(Long reviewId, Long userId);
}