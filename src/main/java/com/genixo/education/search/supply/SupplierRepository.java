package com.genixo.education.search.supply;

import com.genixo.education.search.entity.supply.Supplier;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SupplierRepository extends JpaRepository<Supplier, Long> {

    Optional<Supplier> findByTaxNumber(String taxNumber);

    Boolean existsByTaxNumber(String taxNumber);

    Boolean existsByEmail(String email);

    Boolean existsByTaxNumberAndIdNot(String taxNumber, Long id);

    Boolean existsByEmailAndIdNot(String email, Long id);

    @Query("SELECT s FROM Supplier s WHERE s.isActive = :isActive")
    Page<Supplier> findByIsActive(@Param("isActive") Boolean isActive, Pageable pageable);

    @Query("SELECT s FROM Supplier s WHERE " +
           "(:searchTerm IS NULL OR " +
           "LOWER(s.companyName) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "LOWER(s.taxNumber) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "LOWER(s.email) LIKE LOWER(CONCAT('%', :searchTerm, '%'))) AND " +
           "(:isActive IS NULL OR s.isActive = :isActive)")
    Page<Supplier> searchSuppliers(
            @Param("searchTerm") String searchTerm,
            @Param("isActive") Boolean isActive,
            Pageable pageable
    );

    @Query("SELECT COUNT(s) FROM Supplier s WHERE s.isActive = true")
    Long countActiveSuppliers();

    @Query("SELECT COUNT(s) FROM Supplier s WHERE s.isActive = false")
    Long countInactiveSuppliers();

    @Query("SELECT COUNT(DISTINCT o.supplier.id) FROM Order o WHERE o.company.id = :companyId")
    Long countDistinctByCompanyId(@Param("companyId") Long companyId);

    @Query("SELECT COUNT(DISTINCT o.supplier.id) FROM Order o WHERE o.company.id = :companyId AND o.supplier.isActive = true")
    Long countActiveDistinctByCompanyId(@Param("companyId") Long companyId);
}

