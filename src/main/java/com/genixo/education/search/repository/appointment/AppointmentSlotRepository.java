package com.genixo.education.search.repository.appointment;

import com.genixo.education.search.entity.appointment.AppointmentSlot;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

public interface AppointmentSlotRepository extends JpaRepository<AppointmentSlot, Long> {
    @Query("SELECT as FROM AppointmentSlot as WHERE as.isActive = true AND as.id = :id")
    Optional<AppointmentSlot> findByIdAndIsActiveTrue(@Param("id") Long id);

    @Query("SELECT as FROM AppointmentSlot as " +
            "WHERE as.school.id = :schoolId AND as.isActive = true " +
            "ORDER BY as.dayOfWeek ASC, as.startTime ASC")
    List<AppointmentSlot> findBySchoolIdAndIsActiveTrueOrderByDayOfWeekAscStartTimeAsc(@Param("schoolId") Long schoolId);

    @Query("SELECT as FROM AppointmentSlot as " +
            "WHERE as.school.id = :schoolId " +
            "AND as.staffUser.id = :staffUserId " +
            "AND as.isActive = true " +
            "ORDER BY as.dayOfWeek ASC, as.startTime ASC")
    List<AppointmentSlot> findBySchoolIdAndStaffUserIdAndIsActiveTrue(@Param("schoolId") Long schoolId,
                                                                      @Param("staffUserId") Long staffUserId);

    @Query("SELECT as FROM AppointmentSlot as " +
            "WHERE as.school.id = :schoolId " +
            "AND as.dayOfWeek = :dayOfWeek " +
            "AND as.isActive = true " +
            "ORDER BY as.startTime ASC")
    List<AppointmentSlot> findBySchoolIdAndDayOfWeekAndIsActiveTrue(@Param("schoolId") Long schoolId,
                                                                    @Param("dayOfWeek") DayOfWeek dayOfWeek);

    @Query("SELECT CASE WHEN COUNT(as) > 0 THEN true ELSE false END " +
            "FROM AppointmentSlot as " +
            "WHERE as.school.id = :schoolId " +
            "AND as.dayOfWeek = :dayOfWeek " +
            "AND as.isActive = true " +
            "AND ((:staffUserId IS NULL AND as.staffUser IS NULL) OR as.staffUser.id = :staffUserId) " +
            "AND ((as.startTime <= :startTime AND as.endTime > :startTime) OR " +
            "     (as.startTime < :endTime AND as.endTime >= :endTime) OR " +
            "     (as.startTime >= :startTime AND as.endTime <= :endTime))")
    boolean existsOverlappingSlot(@Param("schoolId") Long schoolId,
                                  @Param("dayOfWeek") DayOfWeek dayOfWeek,
                                  @Param("startTime") LocalTime startTime,
                                  @Param("endTime") LocalTime endTime,
                                  @Param("staffUserId") Long staffUserId);

    @Query("SELECT as FROM AppointmentSlot as " +
            "WHERE as.isActive = true " +
            "AND (as.validUntil IS NOT NULL AND as.validUntil < :currentDate)")
    List<AppointmentSlot> findExpiredSlots(@Param("currentDate") LocalDate currentDate);

    @Modifying
    @Query("UPDATE AppointmentSlot as slt SET slt.isActive = false " +
            "WHERE slt.validUntil IS NOT NULL AND slt.validUntil < :currentDate")
    int deactivateExpiredSlots(@Param("currentDate") LocalDate currentDate);

    @Query("SELECT as FROM AppointmentSlot as " +
            "WHERE as.school.id = :schoolId " +
            "AND as.isActive = true " +
            "AND as.requiresApproval = :requiresApproval " +
            "ORDER BY as.dayOfWeek ASC, as.startTime ASC")
    List<AppointmentSlot> findBySchoolIdAndRequiresApproval(@Param("schoolId") Long schoolId,
                                                            @Param("requiresApproval") Boolean requiresApproval);

    @Query("SELECT as FROM AppointmentSlot as " +
            "WHERE as.school.id = :schoolId " +
            "AND as.isActive = true " +
            "AND as.onlineMeetingAvailable = true " +
            "ORDER BY as.dayOfWeek ASC, as.startTime ASC")
    List<AppointmentSlot> findOnlineSlotsBySchoolId(@Param("schoolId") Long schoolId);
}
