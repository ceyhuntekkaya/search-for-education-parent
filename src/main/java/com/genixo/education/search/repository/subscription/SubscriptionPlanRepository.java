package com.genixo.education.search.repository.subscription;

import com.genixo.education.search.entity.subscription.SubscriptionPlan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

@Repository
public interface SubscriptionPlanRepository extends JpaRepository<SubscriptionPlan, Long> {

    @Query("SELECT sp FROM SubscriptionPlan sp WHERE sp.isActive = true AND sp.isPopular = true ORDER BY sp.sortOrder ASC")
    List<SubscriptionPlan> findPopularPlans();

    @Query("SELECT sp FROM SubscriptionPlan sp WHERE sp.isActive = true AND sp.price <= :maxPrice ORDER BY sp.price ASC")
    List<SubscriptionPlan> findPlansByMaxPrice(@Param("maxPrice") java.math.BigDecimal maxPrice);

    @Query("SELECT COUNT(s) FROM Subscription s WHERE s.subscriptionPlan.id = :planId AND s.status = 'ACTIVE'")
    Long countActiveSubscriptionsByPlan(@Param("planId") Long planId);

    List<SubscriptionPlan> findAllByIsVisibleTrueOrderBySortOrderAsc();

    Optional<SubscriptionPlan> findByIdAndIsActiveTrue(Long id);

    boolean existsByName(String name);
}