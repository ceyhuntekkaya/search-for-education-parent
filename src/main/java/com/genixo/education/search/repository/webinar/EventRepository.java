package com.genixo.education.search.repository.webinar;

import com.genixo.education.search.entity.webinar.Event;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface EventRepository extends JpaRepository<Event, Long> {

    Page<Event> findByOrganizerId(Long organizerId, Pageable pageable);

    Page<Event> findByOrganizerIdAndStatus(Long organizerId, Event.EventStatus status, Pageable pageable);

    List<Event> findByOrganizerId(Long organizerId);

    @Query("SELECT e FROM Event e WHERE " +
            "(:organizerId IS NULL OR e.organizer.id = :organizerId) AND " +
            "(:categoryId IS NULL OR e.category.id = :categoryId) AND " +
            "(:eventType IS NULL OR e.eventType = :eventType) AND " +
            "(:status IS NULL OR e.status = :status) AND " +
            "(:searchTerm IS NULL OR LOWER(e.title) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
            "LOWER(e.description) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
            "LOWER(e.targetAudience) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
            "LOWER(e.speakerName) LIKE LOWER(CONCAT('%', :searchTerm, '%'))) AND " +
            "(:startDateFrom IS NULL OR e.startDateTime >= :startDateFrom) AND " +
            "(:startDateTo IS NULL OR e.startDateTime <= :startDateTo)")
    Page<Event> search(@Param("organizerId") Long organizerId,
                       @Param("categoryId") Long categoryId,
                       @Param("eventType") String eventType,
                       @Param("status") Event.EventStatus status,
                       @Param("searchTerm") String searchTerm,
                       @Param("startDateFrom") LocalDateTime startDateFrom,
                       @Param("startDateTo") LocalDateTime startDateTo,
                       Pageable pageable);
}
