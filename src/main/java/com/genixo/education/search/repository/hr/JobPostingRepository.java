package com.genixo.education.search.repository.hr;

import com.genixo.education.search.entity.hr.JobPosting;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface JobPostingRepository extends JpaRepository<JobPosting, Long> {

    Page<JobPosting> findByCampusId(Long campusId, Pageable pageable);

    Page<JobPosting> findByStatus(String status, Pageable pageable);

    @Query("SELECT j FROM JobPosting j WHERE " +
            "(:schoolId IS NULL OR j.campus.id = :schoolId) AND " +
            "(:branch IS NULL OR LOWER(j.branch) LIKE LOWER(CONCAT('%', :branch, '%'))) AND " +
            "(:status IS NULL OR j.status = :status) AND " +
            "(:searchTerm IS NULL OR LOWER(j.positionTitle) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
            "LOWER(j.description) LIKE LOWER(CONCAT('%', :searchTerm, '%')))")
    Page<JobPosting> search(@Param("schoolId") Long schoolId,
                           @Param("branch") String branch,
                           @Param("status") String status,
                           @Param("searchTerm") String searchTerm,
                           Pageable pageable);
}
