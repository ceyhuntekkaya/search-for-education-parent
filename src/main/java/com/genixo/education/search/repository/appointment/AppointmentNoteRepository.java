package com.genixo.education.search.repository.appointment;

import com.genixo.education.search.entity.appointment.AppointmentNote;
import com.genixo.education.search.enumaration.NoteType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AppointmentNoteRepository extends JpaRepository<AppointmentNote, Long> {
    @Query("SELECT an FROM AppointmentNote an WHERE an.isActive = true AND an.id = :id")
    Optional<AppointmentNote> findByIdAndIsActiveTrue(@Param("id") Long id);

    @Query("SELECT an FROM AppointmentNote an " +
            "WHERE an.appointment.id = :appointmentId AND an.isActive = true " +
            "ORDER BY an.noteDate DESC")
    List<AppointmentNote> findByAppointmentIdAndIsActiveTrueOrderByNoteDateDesc(@Param("appointmentId") Long appointmentId);

    @Query("SELECT an FROM AppointmentNote an " +
            "WHERE an.appointment.id = :appointmentId " +
            "AND an.isActive = true " +
            "AND an.isPrivate = false " +
            "ORDER BY an.noteDate DESC")
    List<AppointmentNote> findPublicNotesByAppointmentId(@Param("appointmentId") Long appointmentId);

    @Query("SELECT an FROM AppointmentNote an " +
            "WHERE an.appointment.id = :appointmentId " +
            "AND an.noteType = :noteType " +
            "AND an.isActive = true " +
            "ORDER BY an.noteDate DESC")
    List<AppointmentNote> findByAppointmentIdAndNoteType(@Param("appointmentId") Long appointmentId,
                                                         @Param("noteType") NoteType noteType);

    @Query("SELECT an FROM AppointmentNote an " +
            "WHERE an.authorUser.id = :authorUserId " +
            "AND an.isActive = true " +
            "ORDER BY an.noteDate DESC")
    List<AppointmentNote> findByAuthorUserIdAndIsActiveTrue(@Param("authorUserId") Long authorUserId);

    @Query("SELECT an FROM AppointmentNote an " +
            "WHERE an.appointment.school.id = :schoolId " +
            "AND an.isActive = true " +
            "AND an.isImportant = true " +
            "ORDER BY an.noteDate DESC")
    List<AppointmentNote> findImportantNotesBySchoolId(@Param("schoolId") Long schoolId);

    @Query("SELECT COUNT(an) FROM AppointmentNote an " +
            "WHERE an.appointment.id = :appointmentId AND an.isActive = true")
    long countByAppointmentId(@Param("appointmentId") Long appointmentId);

    @Query("SELECT COUNT(an) FROM AppointmentNote an " +
            "WHERE an.appointment.id = :appointmentId " +
            "AND an.isActive = true " +
            "AND an.attachmentUrl IS NOT NULL")
    long countNotesWithAttachmentsByAppointmentId(@Param("appointmentId") Long appointmentId);
}
