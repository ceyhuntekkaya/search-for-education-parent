package com.genixo.education.search.repository.subscription;

import com.genixo.education.search.dto.subscription.SubscriptionStatisticsDto;
import com.genixo.education.search.entity.subscription.Subscription;
import com.genixo.education.search.enumaration.SubscriptionStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface SubscriptionRepository extends JpaRepository<Subscription, Long>, JpaSpecificationExecutor<Subscription> {
    @Query("SELECT s FROM Subscription s WHERE s.isActive = true AND s.id = :id")
    Optional<Subscription> findByIdAndIsActiveTrue(@Param("id") Long id);

    @Query("SELECT s FROM Subscription s WHERE s.campus.id = :campusId AND s.isActive = true")
    Optional<Subscription> findByCampusIdAndIsActiveTrue(@Param("campusId") Long campusId);

    @Query("SELECT s FROM Subscription s WHERE s.campus.id = :campusId AND s.status IN :statuses AND s.isActive = true")
    Optional<Subscription> findByCampusIdAndStatusIn(@Param("campusId") Long campusId, @Param("statuses") List<SubscriptionStatus> statuses);

    @Query("SELECT CASE WHEN COUNT(s) > 0 THEN true ELSE false END " +
            "FROM Subscription s WHERE s.campus.id = :campusId AND s.status IN :statuses AND s.isActive = true")
    boolean existsByCampusIdAndStatusIn(@Param("campusId") Long campusId, @Param("statuses") List<SubscriptionStatus> statuses);

    @Query("SELECT s FROM Subscription s WHERE s.nextBillingDate <= :date AND s.status = 'ACTIVE' AND s.autoRenew = true AND s.isActive = true")
    List<Subscription> findSubscriptionsForBilling(@Param("date") LocalDate date);

    @Query("SELECT s FROM Subscription s WHERE " +
            "((s.status = 'CANCELED' AND s.gracePeriodEnd IS NOT NULL AND s.gracePeriodEnd <= :now) OR " +
            "(s.status = 'PAST_DUE' AND s.gracePeriodEnd IS NOT NULL AND s.gracePeriodEnd <= :now)) " +
            "AND s.isActive = true")
    List<Subscription> findExpiredSubscriptions(@Param("now") LocalDateTime now);

    @Query("SELECT s FROM Subscription s WHERE s.trialEndDate IS NOT NULL AND s.trialEndDate <= :date AND s.status = 'TRIAL' AND s.isActive = true")
    List<Subscription> findTrialsEndingOn(@Param("date") LocalDate date);

    @Modifying
    @Query("UPDATE Subscription s SET s.currentMonthAppointments = 0, s.currentMonthPosts = 0 WHERE s.isActive = true")
    int resetMonthlyCounters();

    @Query("SELECT s FROM Subscription s WHERE s.isActive = true AND " +
            "(:status IS NULL OR s.status = :status) AND " +
            "(:planId IS NULL OR s.subscriptionPlan.id = :planId) AND " +
            "(:campusName IS NULL OR LOWER(s.campus.name) LIKE LOWER(CONCAT('%', :campusName, '%')))")
    Page<Subscription> findByFilters(@Param("status") SubscriptionStatus status,
                                     @Param("planId") Long planId,
                                     @Param("campusName") String campusName,
                                     Pageable pageable);

    /* ceyhun
    @Query("SELECT new com.genixo.education.search.dto.subscription.SubscriptionStatisticsDto(" +
            "COUNT(s), " +
            "COUNT(CASE WHEN s.status = 'ACTIVE' THEN 1 END), " +
            "COUNT(CASE WHEN s.status = 'TRIAL' THEN 1 END), " +
            "COUNT(CASE WHEN s.status = 'CANCELED' THEN 1 END), " +
            "COUNT(CASE WHEN s.status = 'PAST_DUE' THEN 1 END), " +
            "COALESCE(SUM(CASE WHEN s.status = 'ACTIVE' THEN s.price ELSE 0 END), 0), " +
            "COALESCE(AVG(CASE WHEN s.status = 'ACTIVE' THEN s.price END), 0)) " +
            "FROM Subscription s WHERE s.isActive = true")
    SubscriptionStatisticsDto getSubscriptionStatistics();

     */

    @Query("SELECT s FROM Subscription s WHERE s.isActive = true AND s.status = 'ACTIVE' " +
            "ORDER BY s.createdAt DESC")
    List<Subscription> findRecentActiveSubscriptions(Pageable pageable);

    @Query("SELECT COUNT(s) FROM Subscription s WHERE s.campus.id = :campusId AND s.isActive = true")
    Long countByCampusId(@Param("campusId") Long campusId);

    @Query("SELECT s FROM Subscription s WHERE s.subscriptionPlan.id = :planId AND s.status = 'ACTIVE' AND s.isActive = true")
    List<Subscription> findActiveSubscriptionsByPlan(@Param("planId") Long planId);

    // Usage tracking queries
    @Modifying
    @Query("UPDATE Subscription s SET s.currentSchoolsCount = s.currentSchoolsCount + :delta WHERE s.campus.id = :campusId AND s.isActive = true")
    void updateSchoolCount(@Param("campusId") Long campusId, @Param("delta") int delta);

    @Modifying
    @Query("UPDATE Subscription s SET s.currentUsersCount = s.currentUsersCount + :delta WHERE s.campus.id = :campusId AND s.isActive = true")
    void updateUserCount(@Param("campusId") Long campusId, @Param("delta") int delta);

    @Modifying
    @Query("UPDATE Subscription s SET s.currentMonthAppointments = s.currentMonthAppointments + :delta WHERE s.campus.id = :campusId AND s.isActive = true")
    void updateAppointmentCount(@Param("campusId") Long campusId, @Param("delta") int delta);

    @Modifying
    @Query("UPDATE Subscription s SET s.currentGalleryItems = s.currentGalleryItems + :delta WHERE s.campus.id = :campusId AND s.isActive = true")
    void updateGalleryItemCount(@Param("campusId") Long campusId, @Param("delta") int delta);

    @Modifying
    @Query("UPDATE Subscription s SET s.currentMonthPosts = s.currentMonthPosts + :delta WHERE s.campus.id = :campusId AND s.isActive = true")
    void updatePostCount(@Param("campusId") Long campusId, @Param("delta") int delta);

    @Modifying
    @Query("UPDATE Subscription s SET s.storageUsedMb = s.storageUsedMb + :deltaMb WHERE s.campus.id = :campusId AND s.isActive = true")
    void updateStorageUsage(@Param("campusId") Long campusId, @Param("deltaMb") long deltaMb);

    // Analytics queries
    @Query("SELECT s FROM Subscription s WHERE s.campus.id IN :campusIds AND s.isActive = true")
    List<Subscription> findByCampusIds(@Param("campusIds") List<Long> campusIds);

    @Query("SELECT COUNT(s) FROM Subscription s WHERE s.createdAt >= :fromDate AND s.isActive = true")
    Long countSubscriptionsCreatedSince(@Param("fromDate") LocalDateTime fromDate);

    @Query("SELECT COUNT(s) FROM Subscription s WHERE s.canceledAt >= :fromDate AND s.isActive = true")
    Long countSubscriptionsCanceledSince(@Param("fromDate") LocalDateTime fromDate);
}