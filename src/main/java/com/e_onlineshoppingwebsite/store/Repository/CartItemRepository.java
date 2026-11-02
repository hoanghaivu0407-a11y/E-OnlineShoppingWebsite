package com.e_onlineshoppingwebsite.store.Repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.e_onlineshoppingwebsite.store.Model.CartItem;

@Repository
public interface CartItemRepository extends JpaRepository<CartItem, Long> {

        List<CartItem> findByCart_CartId(Long cartId);

        Optional<CartItem> findByCart_CartIdAndProduct_ProductId(
                        Long cartId,
                        Long productId);

        boolean existsByCart_CartIdAndProduct_ProductId(
                        Long cartId,
                        Long productId);

        void deleteByCart_CartId(Long cartId);

        long countByCart_CartId(Long cartId);
}