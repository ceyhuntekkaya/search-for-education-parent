package com.genixo.education.search.repository.webinar;

import com.genixo.education.search.entity.webinar.EventRegistration;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface EventRegistrationRepository extends JpaRepository<EventRegistration, Long> {

    Optional<EventRegistration> findByEventIdAndTeacherId(Long eventId, Long teacherId);

    Boolean existsByEventIdAndTeacherId(Long eventId, Long teacherId);

    Page<EventRegistration> findByEventId(Long eventId, Pageable pageable);

    Page<EventRegistration> findByTeacherId(Long teacherId, Pageable pageable);

    Page<EventRegistration> findByEventIdAndStatus(Long eventId, EventRegistration.RegistrationStatus status, Pageable pageable);

    @Query("SELECT r FROM EventRegistration r WHERE " +
            "(:eventId IS NULL OR r.event.id = :eventId) AND " +
            "(:teacherId IS NULL OR r.teacher.id = :teacherId) AND " +
            "(:status IS NULL OR r.status = :status)")
    Page<EventRegistration> search(@Param("eventId") Long eventId,
                                   @Param("teacherId") Long teacherId,
                                   @Param("status") EventRegistration.RegistrationStatus status,
                                   Pageable pageable);
}
