package com.genixo.education.search.repository.hr;

import com.genixo.education.search.entity.hr.Application;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ApplicationRepository extends JpaRepository<Application, Long> {

    Page<Application> findByJobPostingId(Long jobPostingId, Pageable pageable);

    Page<Application> findByTeacherId(Long teacherId, Pageable pageable);

    boolean existsByJobPostingIdAndTeacherId(Long jobPostingId, Long teacherId);

    @Query("SELECT a FROM Application a WHERE a.jobPosting.campus.id = :campusId")
    Page<Application> findByCampusId(@Param("campusId") Long campusId, Pageable pageable);

    @Query("SELECT a FROM Application a WHERE " +
            "(:jobPostingId IS NULL OR a.jobPosting.id = :jobPostingId) AND " +
            "(:teacherId IS NULL OR a.teacher.id = :teacherId) AND " +
            "(:status IS NULL OR a.status = :status)")
    Page<Application> search(@Param("jobPostingId") Long jobPostingId,
                            @Param("teacherId") Long teacherId,
                            @Param("status") String status,
                            Pageable pageable);
}
