package com.genixo.education.search.repository.appointment;

import com.genixo.education.search.entity.appointment.ParentSchoolList;
import com.genixo.education.search.entity.institution.School;
import com.genixo.education.search.enumaration.ListStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
public interface ParentSchoolListRepository extends JpaRepository<ParentSchoolList, Long> {

    List<ParentSchoolList> findByParentUserIdAndStatusOrderByIsDefaultDescCreatedAtDesc(Long parentUserId, ListStatus status);

    List<ParentSchoolList> findByParentUserIdAndStatusInOrderByIsDefaultDescCreatedAtDesc(Long parentUserId, List<ListStatus> statuses);

    Optional<ParentSchoolList> findByParentUserIdAndIsDefaultTrueAndStatus(Long parentUserId, ListStatus status);

    List<ParentSchoolList> findByParentUserIdOrderByIsDefaultDescLastAccessedAtDesc(Long parentUserId);

    @Query("SELECT psl FROM ParentSchoolList psl WHERE psl.parentUser.id = :parentUserId AND " +
            "(psl.listName ILIKE %:keyword% OR psl.description ILIKE %:keyword%)")
    List<ParentSchoolList> findByParentUserIdAndKeyword(@Param("parentUserId") Long parentUserId,
                                                        @Param("keyword") String keyword);

    boolean existsByParentUserIdAndListName(Long parentUserId, String listName);

    boolean existsByParentUserIdAndListNameAndIdNot(Long parentUserId, String listName, Long id);

    @Query("SELECT COUNT(psl) FROM ParentSchoolList psl WHERE psl.parentUser.id = :parentUserId AND psl.status = :status")
    Integer countByParentUserIdAndStatus(@Param("parentUserId") Long parentUserId, @Param("status") ListStatus status);

    @Query("SELECT item.school FROM ParentSchoolListItem item " +
            "JOIN item.parentSchoolList list " +
            "WHERE list.id = :listId")
    List<School> findSchoolsByListId(@Param("listId") Long listId);
}