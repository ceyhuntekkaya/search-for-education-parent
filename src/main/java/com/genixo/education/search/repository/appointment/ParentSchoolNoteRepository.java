package com.genixo.education.search.repository.appointment;

import com.genixo.education.search.entity.appointment.ParentSchoolNote;
import com.genixo.education.search.enumaration.NoteCategory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ParentSchoolNoteRepository extends JpaRepository<ParentSchoolNote, Long> {

    List<ParentSchoolNote> findBySchoolIdAndParentSchoolListItem_ParentSchoolList_ParentUserIdOrderByCreatedAtDesc(
            Long schoolId, Long parentUserId);

    List<ParentSchoolNote> findByParentSchoolListItemIdOrderByCreatedAtDesc(Long parentSchoolListItemId);

    Page<ParentSchoolNote> findByParentSchoolListItem_ParentSchoolList_ParentUserIdOrderByCreatedAtDesc(
            Long parentUserId, Pageable pageable);

    @Query("SELECT psn FROM ParentSchoolNote psn " +
            "WHERE psn.parentSchoolListItem.parentSchoolList.parentUser.id = :parentUserId " +
            "AND (:category IS NULL OR psn.category = :category) " +
            "AND (:onlyImportant = false OR psn.isImportant = true) " +
            "AND (:keyword IS NULL OR psn.noteTitle ILIKE %:keyword% OR psn.noteContent ILIKE %:keyword%)")
    Page<ParentSchoolNote> findWithFilters(
            @Param("parentUserId") Long parentUserId,
            @Param("category") NoteCategory category,
            @Param("onlyImportant") Boolean onlyImportant,
            @Param("keyword") String keyword,
            Pageable pageable);

    @Query("SELECT psn FROM ParentSchoolNote psn " +
            "WHERE psn.parentSchoolListItem.parentSchoolList.parentUser.id = :parentUserId " +
            "AND psn.reminderDate BETWEEN :startDate AND :endDate " +
            "ORDER BY psn.reminderDate ASC")
    List<ParentSchoolNote> findUpcomingReminders(
            @Param("parentUserId") Long parentUserId,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate);

    @Query("SELECT COUNT(psn) FROM ParentSchoolNote psn " +
            "WHERE psn.parentSchoolListItem.id = :listItemId")
    Integer countByParentSchoolListItemId(@Param("listItemId") Long listItemId);

    @Query("SELECT COUNT(psn) FROM ParentSchoolNote psn " +
            "WHERE psn.parentSchoolListItem.id = :listItemId AND psn.isImportant = true")
    Integer countImportantByParentSchoolListItemId(@Param("listItemId") Long listItemId);
}