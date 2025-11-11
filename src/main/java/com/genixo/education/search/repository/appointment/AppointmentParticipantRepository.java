package com.genixo.education.search.repository.appointment;

import com.genixo.education.search.entity.appointment.AppointmentParticipant;
import com.genixo.education.search.enumaration.AttendanceStatus;
import com.genixo.education.search.enumaration.ParticipantType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AppointmentParticipantRepository extends JpaRepository<AppointmentParticipant, Long> {
    @Query("SELECT ap FROM AppointmentParticipant ap WHERE ap.isActive = true AND ap.id = :id")
    Optional<AppointmentParticipant> findByIdAndIsActiveTrue(@Param("id") Long id);

    @Query("SELECT ap FROM AppointmentParticipant ap " +
            "WHERE ap.appointment.id = :appointmentId AND ap.isActive = true " +
            "ORDER BY ap.participantType ASC, ap.name ASC")
    List<AppointmentParticipant> findByAppointmentIdAndIsActiveTrue(@Param("appointmentId") Long appointmentId);

    @Query("SELECT ap FROM AppointmentParticipant ap " +
            "WHERE ap.appointment.id = :appointmentId " +
            "AND ap.participantType = :participantType " +
            "AND ap.isActive = true " +
            "ORDER BY ap.name ASC")
    List<AppointmentParticipant> findByAppointmentIdAndParticipantType(@Param("appointmentId") Long appointmentId,
                                                                       @Param("participantType") ParticipantType participantType);

    @Query("SELECT ap FROM AppointmentParticipant ap " +
            "WHERE ap.user.id = :userId AND ap.isActive = true " +
            "ORDER BY ap.appointment.appointmentDate DESC")
    List<AppointmentParticipant> findByUserIdAndIsActiveTrue(@Param("userId") Long userId);

    @Modifying
    @Query("UPDATE AppointmentParticipant ap SET ap.attendanceStatus = :status " +
            "WHERE ap.appointment.id = :appointmentId")
    int updateAttendanceStatusByAppointmentId(@Param("appointmentId") Long appointmentId,
                                              @Param("status") AttendanceStatus status);

    @Query("SELECT COUNT(ap) FROM AppointmentParticipant ap " +
            "WHERE ap.appointment.id = :appointmentId AND ap.isActive = true")
    long countByAppointmentId(@Param("appointmentId") Long appointmentId);

    @Query("SELECT COUNT(ap) FROM AppointmentParticipant ap " +
            "WHERE ap.appointment.id = :appointmentId " +
            "AND ap.attendanceStatus = :status " +
            "AND ap.isActive = true")
    long countByAppointmentIdAndAttendanceStatus(@Param("appointmentId") Long appointmentId,
                                                 @Param("status") AttendanceStatus status);
}
