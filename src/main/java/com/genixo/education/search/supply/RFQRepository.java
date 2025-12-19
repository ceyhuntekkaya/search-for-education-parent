package com.genixo.education.search.supply;

import com.genixo.education.search.entity.supply.RFQ;
import com.genixo.education.search.enumaration.RFQStatus;
import com.genixo.education.search.enumaration.RFQType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface RFQRepository extends JpaRepository<RFQ, Long> {

    Page<RFQ> findByCompanyId(Long companyId, Pageable pageable);

    @Query("SELECT r FROM RFQ r WHERE r.status = :status")
    Page<RFQ> findByStatus(@Param("status") RFQStatus status, Pageable pageable);

    @Query("SELECT r FROM RFQ r WHERE r.status = 'PUBLISHED' AND " +
           "(r.submissionDeadline IS NULL OR r.submissionDeadline >= :currentDate)")
    List<RFQ> findActiveRFQs(@Param("currentDate") LocalDate currentDate);

    @Query("SELECT r FROM RFQ r WHERE " +
           "(:searchTerm IS NULL OR " +
           "LOWER(r.title) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "LOWER(r.description) LIKE LOWER(CONCAT('%', :searchTerm, '%'))) AND " +
           "(:companyId IS NULL OR r.company.id = :companyId) AND " +
           "(:status IS NULL OR r.status = :status) AND " +
           "(:rfqType IS NULL OR r.rfqType = :rfqType)")
    Page<RFQ> searchRFQs(
            @Param("searchTerm") String searchTerm,
            @Param("companyId") Long companyId,
            @Param("status") RFQStatus status,
            @Param("rfqType") RFQType rfqType,
            Pageable pageable
    );

    @Query("SELECT COUNT(r) FROM RFQ r WHERE r.company.id = :companyId")
    Long countByCompanyId(@Param("companyId") Long companyId);

    @Query("SELECT COUNT(r) FROM RFQ r WHERE r.company.id = :companyId AND r.status = :status")
    Long countByCompanyIdAndStatus(@Param("companyId") Long companyId, @Param("status") RFQStatus status);
}

