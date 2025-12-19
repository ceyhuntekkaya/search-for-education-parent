package com.genixo.education.search.repository.supply;

import com.genixo.education.search.entity.supply.RFQInvitation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RFQInvitationRepository extends JpaRepository<RFQInvitation, Long> {

    List<RFQInvitation> findByRfqId(Long rfqId);

    @Query("SELECT ri FROM RFQInvitation ri WHERE ri.rfq.id = :rfqId AND ri.supplier.id = :supplierId")
    Optional<RFQInvitation> findByRfqIdAndSupplierId(@Param("rfqId") Long rfqId, @Param("supplierId") Long supplierId);

    Boolean existsByRfqIdAndSupplierId(Long rfqId, Long supplierId);
}

