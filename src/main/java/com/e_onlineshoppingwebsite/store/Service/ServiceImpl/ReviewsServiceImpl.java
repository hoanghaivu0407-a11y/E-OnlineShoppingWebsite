
package com.e_onlineshoppingwebsite.store.Service.ServiceImpl;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.e_onlineshoppingwebsite.store.Model.OrderItem;
import com.e_onlineshoppingwebsite.store.Model.Review;
import com.e_onlineshoppingwebsite.store.Repository.OrderItemRepository;
import com.e_onlineshoppingwebsite.store.Repository.ReviewRepository;
import com.e_onlineshoppingwebsite.store.Service.ReviewsService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class ReviewsServiceImpl implements ReviewsService {

    private final ReviewRepository reviewRepository;
    private final OrderItemRepository orderItemRepository;

    @Override
    public List<Review> getAllReviews() {
        return reviewRepository.findAll();
    }

    @Override
    public Review getReviewById(Long id) {
        return reviewRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy đánh giá"));
    }

    @Override
    public List<Review> getReviewsByProductId(Long productId) {
        return reviewRepository.findByProduct_ProductId(productId);
    }

    @Override
    public List<Review> getReviewsByUserId(Long userId) {
        return reviewRepository.findByMember_UserId(userId);
    }

    @Override
    public List<Review> findByRating(Long productId, int rating) {
        return reviewRepository.findByProduct_ProductIdAndRating(
                productId, rating);
    }

    @Override
    public double calculateAverageRating(Long productId) {
        Double average = reviewRepository.calculateAverageRating(productId);

        return average == null ? 0.0 : average;
    }

    @Override
    public long countReviewsByProduct(Long productId) {
        return reviewRepository.countByProduct_ProductId(productId);
    }

    @Override
    public boolean canReviewProduct(Long userId, Long productId) {

        List<OrderItem> items = orderItemRepository.findByProduct_ProductId(productId);

        return items.stream().anyMatch(item -> item.getOrder().getMember().getUserId().equals(userId)
                && "completed".equals(item.getOrder().getOrderStatus()));
    }

    @Override
    public void save(Review review) {

        if (review == null || review.getMember() == null
                || review.getProduct() == null
                || review.getRating() == null
                || review.getRating() < 1 || review.getRating() > 5) {
            throw new IllegalArgumentException("Đánh giá không hợp lệ");
        }

        Long userId = review.getMember().getUserId();
        Long productId = review.getProduct().getProductId();

        if (!canReviewProduct(userId, productId)) {
            throw new IllegalArgumentException(
                    "Bạn chưa mua và hoàn thành đơn hàng chứa sản phẩm");
        }

        review.setReviewId(null);
        reviewRepository.save(review);
    }

    @Override
    public void update(Review review) {

        if (review == null || review.getReviewId() == null) {
            throw new IllegalArgumentException("Thiếu ID đánh giá");
        }

        if (review.getRating() == null
                || review.getRating() < 1 || review.getRating() > 5) {
            throw new IllegalArgumentException("Số sao phải từ 1 đến 5");
        }

        Review old = getReviewById(review.getReviewId());

        old.setRating(review.getRating());
        old.setComment(review.getComment());

        reviewRepository.save(old);
    }

    @Override
    public void deleteReview(Long reviewId, Long userId) {

        Review review = getReviewById(reviewId);

        if (!review.getMember().getUserId().equals(userId)) {
            throw new IllegalArgumentException(
                    "Bạn không có quyền xóa đánh giá này");
        }

        reviewRepository.delete(review);
    }
}
