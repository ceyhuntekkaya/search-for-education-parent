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

    @Query(value = "SELECT jp.* FROM job_postings jp WHERE " +
            "(:schoolId IS NULL OR jp.school_id = :schoolId) AND " +
            "(:branch IS NULL OR LOWER(CAST(jp.branch AS TEXT)) LIKE LOWER(('%' || CAST(:branch AS TEXT) || '%'))) AND " +
            "(:status IS NULL OR CAST(jp.status AS TEXT) = CAST(:status AS TEXT)) AND " +
            "(:searchTerm IS NULL OR LOWER(CAST(jp.position_title AS TEXT)) LIKE LOWER(('%' || CAST(:searchTerm AS TEXT) || '%')) OR " +
            "LOWER(CAST(jp.description AS TEXT)) LIKE LOWER(('%' || CAST(:searchTerm AS TEXT) || '%')))",
            countQuery = "SELECT COUNT(jp.id) FROM job_postings jp WHERE " +
            "(:schoolId IS NULL OR jp.school_id = :schoolId) AND " +
            "(:branch IS NULL OR LOWER(CAST(jp.branch AS TEXT)) LIKE LOWER(('%' || CAST(:branch AS TEXT) || '%'))) AND " +
            "(:status IS NULL OR CAST(jp.status AS TEXT) = CAST(:status AS TEXT)) AND " +
            "(:searchTerm IS NULL OR LOWER(CAST(jp.position_title AS TEXT)) LIKE LOWER(('%' || CAST(:searchTerm AS TEXT) || '%')) OR " +
            "LOWER(CAST(jp.description AS TEXT)) LIKE LOWER(('%' || CAST(:searchTerm AS TEXT) || '%')))",
            nativeQuery = true)
    Page<JobPosting> search(@Param("schoolId") Long schoolId,
                            @Param("branch") String branch,
                            @Param("status") String status,
                            @Param("searchTerm") String searchTerm,
                            Pageable pageable);
}
