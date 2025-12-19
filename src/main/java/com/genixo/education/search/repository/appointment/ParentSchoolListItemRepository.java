package com.genixo.education.search.repository.appointment;

import com.genixo.education.search.entity.appointment.ParentSchoolListItem;
import com.genixo.education.search.enumaration.SchoolItemStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface ParentSchoolListItemRepository extends JpaRepository<ParentSchoolListItem, Long> {

    List<ParentSchoolListItem> findByParentSchoolListIdAndStatusOrderByPriorityOrderAscCreatedAtDesc(
            Long parentSchoolListId, SchoolItemStatus status);

    Page<ParentSchoolListItem> findByParentSchoolListIdAndStatus(
            Long parentSchoolListId, SchoolItemStatus status, Pageable pageable);

    Optional<ParentSchoolListItem> findByParentSchoolListIdAndSchoolId(Long parentSchoolListId, Long schoolId);

    boolean existsByParentSchoolListIdAndSchoolId(Long parentSchoolListId, Long schoolId);

    List<ParentSchoolListItem> findBySchoolIdAndParentSchoolList_ParentUserIdAndStatus(
            Long schoolId, Long parentUserId, SchoolItemStatus status);

    @Query("SELECT psli FROM ParentSchoolListItem psli " +
            "WHERE psli.parentSchoolList.parentUser.id = :parentUserId " +
            "AND psli.status = :status " +
            "AND (:onlyFavorites = false OR psli.isFavorite = true) " +
            "AND (:onlyBlocked = false OR psli.isBlocked = true) " +
            "AND (:minRating IS NULL OR psli.starRating >= :minRating) " +
            "AND (:maxRating IS NULL OR psli.starRating <= :maxRating) " +
            "AND (:keyword IS NULL OR psli.school.name ILIKE %:keyword% OR psli.personalNotes ILIKE %:keyword% OR psli.tags ILIKE %:keyword%)")
    Page<ParentSchoolListItem> findWithFilters(
            @Param("parentUserId") Long parentUserId,
            @Param("status") SchoolItemStatus status,
            @Param("onlyFavorites") Boolean onlyFavorites,
            @Param("onlyBlocked") Boolean onlyBlocked,
            @Param("minRating") Integer minRating,
            @Param("maxRating") Integer maxRating,
            @Param("keyword") String keyword,
            Pageable pageable);

    @Query("SELECT psli FROM ParentSchoolListItem psli " +
            "WHERE psli.parentSchoolList.parentUser.id = :parentUserId " +
            "AND psli.visitPlannedDate BETWEEN :startDate AND :endDate " +
            "AND psli.visitCompletedDate IS NULL")
    List<ParentSchoolListItem> findUpcomingVisits(
            @Param("parentUserId") Long parentUserId,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate);

    @Query("SELECT COUNT(psli) FROM ParentSchoolListItem psli " +
            "WHERE psli.parentSchoolList.parentUser.id = :parentUserId " +
            "AND psli.status = 'ACTIVE' AND psli.isFavorite = true")
    Integer countFavoritesByParentUserId(@Param("parentUserId") Long parentUserId);

    @Query("SELECT COUNT(psli) FROM ParentSchoolListItem psli " +
            "WHERE psli.parentSchoolList.parentUser.id = :parentUserId " +
            "AND psli.status = 'ACTIVE' AND psli.isBlocked = true")
    Integer countBlockedByParentUserId(@Param("parentUserId") Long parentUserId);

    List<ParentSchoolListItem> findByParentSchoolList_ParentUserIdAndSchoolIdIn(Long parentUserId, List<Long> schoolIds);

    @Query("SELECT psli FROM ParentSchoolListItem psli " +
            "WHERE psli.parentSchoolList.parentUser.id = :parentUserId " +
            "ORDER BY psli.createdAt DESC")
    List<ParentSchoolListItem> findRecentlyAddedByParentUserId(@Param("parentUserId") Long parentUserId, Pageable pageable);

    @Modifying
    @Query("DELETE FROM ParentSchoolListItem psli WHERE psli.school.id = :schoolId")
    void deleteBySchoolId(@Param("schoolId") Long schoolId);
}
