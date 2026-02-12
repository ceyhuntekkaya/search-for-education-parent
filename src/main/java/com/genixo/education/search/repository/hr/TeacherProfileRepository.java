package com.genixo.education.search.repository.hr;

import com.genixo.education.search.entity.hr.TeacherProfile;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TeacherProfileRepository extends JpaRepository<TeacherProfile, Long> {

    Optional<TeacherProfile> findByUserId(Long userId);

    boolean existsByUserId(Long userId);

    boolean existsByEmail(String email);

    boolean existsByEmailAndIdNot(String email, Long id);

    @Query("SELECT t FROM TeacherProfile t WHERE " +
            "(:branch IS NULL OR LOWER(t.branch) LIKE LOWER(CONCAT('%', :branch, '%'))) AND " +
            "(:searchTerm IS NULL OR LOWER(t.fullName) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
            "LOWER(t.email) LIKE LOWER(CONCAT('%', :searchTerm, '%')))")
    Page<TeacherProfile> search(@Param("branch") String branch,
                               @Param("searchTerm") String searchTerm,
                               Pageable pageable);
}
