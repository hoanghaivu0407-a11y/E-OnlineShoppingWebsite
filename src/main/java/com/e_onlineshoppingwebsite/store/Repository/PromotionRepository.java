package com.e_onlineshoppingwebsite.store.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.e_onlineshoppingwebsite.store.Model.Promotion;

@Repository
public interface PromotionRepository extends JpaRepository<Promotion, Long> {

    Optional<Promotion> findByCode(String code);

    boolean existsByCode(String code);

    List<Promotion> findByIsActiveTrue();

    List<Promotion> findByIsActiveTrueAndStartDateLessThanEqualAndEndDateGreaterThanEqual(
            LocalDateTime startDate,
            LocalDateTime endDate);
}