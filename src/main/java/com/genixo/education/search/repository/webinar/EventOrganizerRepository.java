package com.genixo.education.search.repository.webinar;

import com.genixo.education.search.entity.webinar.EventOrganizer;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface EventOrganizerRepository extends JpaRepository<EventOrganizer, Long> {

    Optional<EventOrganizer> findBySlug(String slug);

    Boolean existsBySlug(String slug);

    Page<EventOrganizer> findByIsActiveTrue(Pageable pageable);

    @Query("SELECT e FROM EventOrganizer e WHERE " +
            "(:type IS NULL OR e.type = :type) AND " +
            "(:searchTerm IS NULL OR LOWER(e.name) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
            "LOWER(e.description) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
            "LOWER(e.slug) LIKE LOWER(CONCAT('%', :searchTerm, '%'))) AND " +
            "(:isActive IS NULL OR e.isActive = :isActive)")
    Page<EventOrganizer> search(@Param("type") String type,
                                @Param("searchTerm") String searchTerm,
                                @Param("isActive") Boolean isActive,
                                Pageable pageable);
}
