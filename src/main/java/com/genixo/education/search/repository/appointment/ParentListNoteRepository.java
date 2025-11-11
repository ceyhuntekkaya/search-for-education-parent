package com.genixo.education.search.repository.appointment;

import com.genixo.education.search.entity.appointment.ParentListNote;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ParentListNoteRepository extends JpaRepository<ParentListNote, Long> {

    List<ParentListNote> findByParentSchoolListIdOrderByCreatedAtDesc(Long parentSchoolListId);

    Page<ParentListNote> findByParentSchoolListIdOrderByCreatedAtDesc(Long parentSchoolListId, Pageable pageable);

    @Query("SELECT pln FROM ParentListNote pln " +
            "WHERE pln.parentSchoolList.parentUser.id = :parentUserId " +
            "AND (:onlyImportant = false OR pln.isImportant = true) " +
            "AND (:keyword IS NULL OR pln.noteTitle ILIKE %:keyword% OR pln.noteContent ILIKE %:keyword%)")
    Page<ParentListNote> findWithFilters(
            @Param("parentUserId") Long parentUserId,
            @Param("onlyImportant") Boolean onlyImportant,
            @Param("keyword") String keyword,
            Pageable pageable);

    @Query("SELECT pln FROM ParentListNote pln " +
            "WHERE pln.parentSchoolList.parentUser.id = :parentUserId " +
            "AND pln.reminderDate BETWEEN :startDate AND :endDate " +
            "ORDER BY pln.reminderDate ASC")
    List<ParentListNote> findUpcomingReminders(
            @Param("parentUserId") Long parentUserId,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate);
}