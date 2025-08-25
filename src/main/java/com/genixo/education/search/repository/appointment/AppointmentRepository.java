package com.genixo.education.search.repository.appointment;

import com.genixo.education.search.dto.appointment.*;
import com.genixo.education.search.entity.appointment.Appointment;
import com.genixo.education.search.enumaration.AppointmentOutcome;
import com.genixo.education.search.enumaration.AppointmentStatus;
import com.genixo.education.search.enumaration.AppointmentType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
public interface AppointmentRepository extends JpaRepository<Appointment, Long> {

    @Query("SELECT a FROM Appointment a WHERE a.isActive = true AND a.id = :id")
    Optional<Appointment> findByIdAndIsActiveTrue(@Param("id") Long id);

    @Query("SELECT a FROM Appointment a WHERE a.isActive = true AND a.appointmentNumber = :appointmentNumber")
    Optional<Appointment> findByAppointmentNumberAndIsActiveTrue(@Param("appointmentNumber") String appointmentNumber);

    @Query("SELECT a FROM Appointment a " +
            "WHERE a.isActive = true " +
            "AND a.school.id = :schoolId " +
            "AND a.appointmentDate BETWEEN :startDate AND :endDate " +
            "ORDER BY a.appointmentDate ASC, a.startTime ASC")
    List<Appointment> findBySchoolIdAndDateRange(@Param("schoolId") Long schoolId,
                                                 @Param("startDate") LocalDate startDate,
                                                 @Param("endDate") LocalDate endDate);

    @Query("SELECT a FROM Appointment a " +
            "WHERE a.isActive = true " +
            "AND a.parentUser.id = :parentUserId " +
            "ORDER BY a.appointmentDate DESC, a.startTime DESC")
    List<Appointment> findByParentUserIdAndIsActiveTrue(@Param("parentUserId") Long parentUserId);

    @Query("SELECT a FROM Appointment a " +
            "WHERE a.isActive = true " +
            "AND a.staffUser.id = :staffUserId " +
            "ORDER BY a.appointmentDate ASC, a.startTime ASC")
    List<Appointment> findByStaffUserIdAndIsActiveTrue(@Param("staffUserId") Long staffUserId);

    @Query("SELECT COUNT(a) FROM Appointment a " +
            "WHERE a.isActive = true " +
            "AND a.appointmentSlot.id = :slotId " +
            "AND a.appointmentDate = :appointmentDate " +
            "AND a.startTime = :startTime " +
            "AND a.status IN ('CONFIRMED', 'PENDING', 'IN_PROGRESS')")
    long countBookedAppointments(@Param("slotId") Long slotId,
                                 @Param("appointmentDate") LocalDate appointmentDate,
                                 @Param("startTime") LocalTime startTime);

    @Query("SELECT CASE WHEN COUNT(a) > 0 THEN true ELSE false END " +
            "FROM Appointment a " +
            "WHERE a.isActive = true " +
            "AND a.school.id = :schoolId " +
            "AND LOWER(a.parentEmail) = LOWER(:parentEmail) " +
            "AND a.status = 'WAITING'")
    boolean existsInWaitlist(@Param("schoolId") Long schoolId, @Param("parentEmail") String parentEmail);

    // Complex search query
    @Query("SELECT DISTINCT a FROM Appointment a " +
            "LEFT JOIN a.school s " +
            "LEFT JOIN a.staffUser su " +
            "LEFT JOIN a.parentUser pu " +
            "WHERE a.isActive = true " +
            "AND (:searchTerm IS NULL OR " +
            "    LOWER(a.parentName) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
            "    LOWER(a.studentName) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
            "    LOWER(a.appointmentNumber) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
            "    LOWER(s.name) LIKE LOWER(CONCAT('%', :searchTerm, '%'))) " +
            "AND (:schoolIds IS NULL OR a.school.id IN :schoolIds) " +
            "AND (:staffUserIds IS NULL OR a.staffUser.id IN :staffUserIds) " +
            "AND (:statuses IS NULL OR a.status IN :statuses) " +
            "AND (:appointmentTypes IS NULL OR a.appointmentType IN :appointmentTypes) " +
            "AND (:appointmentDateFrom IS NULL OR a.appointmentDate >= :appointmentDateFrom) " +
            "AND (:appointmentDateTo IS NULL OR a.appointmentDate <= :appointmentDateTo) " +
            "AND (:createdFrom IS NULL OR a.createdAt >= :createdFrom) " +
            "AND (:createdTo IS NULL OR a.createdAt <= :createdTo) " +
            "AND (:parentEmail IS NULL OR LOWER(a.parentEmail) LIKE LOWER(CONCAT('%', :parentEmail, '%'))) " +
            "AND (:parentPhone IS NULL OR a.parentPhone LIKE CONCAT('%', :parentPhone, '%')) " +
            "AND (:studentName IS NULL OR LOWER(a.studentName) LIKE LOWER(CONCAT('%', :studentName, '%'))) " +
            "AND (:gradeInterested IS NULL OR LOWER(a.gradeInterested) LIKE LOWER(CONCAT('%', :gradeInterested, '%'))) " +
            "AND (:outcomes IS NULL OR a.outcome IN :outcomes) " +
            "AND (:followUpRequired IS NULL OR a.followUpRequired = :followUpRequired) " +
            "AND (:isOnline IS NULL OR a.isOnline = :isOnline) " +
            "AND (:hasNotes IS NULL OR " +
            "    (:hasNotes = true AND (a.notes IS NOT NULL OR a.internalNotes IS NOT NULL)) OR " +
            "    (:hasNotes = false AND a.notes IS NULL AND a.internalNotes IS NULL)) " +
            "AND (:rescheduleCountMin IS NULL OR a.rescheduleCount >= :rescheduleCountMin) " +
            "AND (:rescheduleCountMax IS NULL OR a.rescheduleCount <= :rescheduleCountMax)")
    Page<Appointment> searchAppointments(
            @Param("searchTerm") String searchTerm,
            @Param("schoolIds") List<Long> schoolIds,
            @Param("staffUserIds") List<Long> staffUserIds,
            @Param("statuses") List<AppointmentStatus> statuses,
            @Param("appointmentTypes") List<AppointmentType> appointmentTypes,
            @Param("appointmentDateFrom") LocalDate appointmentDateFrom,
            @Param("appointmentDateTo") LocalDate appointmentDateTo,
            @Param("createdFrom") LocalDateTime createdFrom,
            @Param("createdTo") LocalDateTime createdTo,
            @Param("parentEmail") String parentEmail,
            @Param("parentPhone") String parentPhone,
            @Param("studentName") String studentName,
            @Param("gradeInterested") String gradeInterested,
            @Param("outcomes") List<AppointmentOutcome> outcomes,
            @Param("followUpRequired") Boolean followUpRequired,
            @Param("isOnline") Boolean isOnline,
            @Param("hasNotes") Boolean hasNotes,
            @Param("rescheduleCountMin") Integer rescheduleCountMin,
            @Param("rescheduleCountMax") Integer rescheduleCountMax,
            Pageable pageable);

    // Statistics and Analytics
    @Query("SELECT new com.genixo.education.search.dto.appointment.AppointmentStatisticsDto(" +
            "a.school.id, a.school.name, :periodStart, :periodEnd, " +
            "COUNT(a), " +
            "SUM(CASE WHEN a.status = 'COMPLETED' THEN 1 ELSE 0 END), " +
            "SUM(CASE WHEN a.status = 'CANCELLED' THEN 1 ELSE 0 END), " +
            "SUM(CASE WHEN a.status = 'NO_SHOW' THEN 1 ELSE 0 END), " +
            "SUM(CASE WHEN a.status = 'RESCHEDULED' THEN 1 ELSE 0 END), " +
            "CASE WHEN COUNT(a) > 0 THEN CAST(SUM(CASE WHEN a.status = 'COMPLETED' THEN 1 ELSE 0 END) AS DOUBLE) / COUNT(a) * 100 ELSE 0.0 END, " +
            "CASE WHEN COUNT(a) > 0 THEN CAST(SUM(CASE WHEN a.status = 'CANCELLED' THEN 1 ELSE 0 END) AS DOUBLE) / COUNT(a) * 100 ELSE 0.0 END, " +
            "CASE WHEN COUNT(a) > 0 THEN CAST(SUM(CASE WHEN a.status = 'NO_SHOW' THEN 1 ELSE 0 END) AS DOUBLE) / COUNT(a) * 100 ELSE 0.0 END, " +
            "CASE WHEN COUNT(a) > 0 THEN CAST(SUM(CASE WHEN a.status = 'RESCHEDULED' THEN 1 ELSE 0 END) AS DOUBLE) / COUNT(a) * 100 ELSE 0.0 END, " +
            "CASE WHEN SUM(CASE WHEN a.status = 'COMPLETED' THEN 1 ELSE 0 END) > 0 THEN " +
            "     CAST(SUM(CASE WHEN a.outcome = 'ENROLLED' THEN 1 ELSE 0 END) AS DOUBLE) / SUM(CASE WHEN a.status = 'COMPLETED' THEN 1 ELSE 0 END) * 100 " +
            "     ELSE 0.0 END, " +
            "SUM(CASE WHEN a.outcome = 'ENROLLED' THEN 1 ELSE 0 END), " +
            "SUM(CASE WHEN a.outcome = 'INTERESTED' THEN 1 ELSE 0 END), " +
            "SUM(CASE WHEN a.outcome = 'NOT_INTERESTED' THEN 1 ELSE 0 END), " +
            "AVG(COALESCE(a.enrollmentLikelihood, 0)), " +
            "0.0, " +
            "CAST(AVG(COALESCE(a.rescheduleCount, 0)) AS INTEGER), " +
            "0.0, " +
            "'', " +
            "'', " +
            "null, " +
            "'') " +
            "FROM Appointment a " +
            "WHERE a.isActive = true " +
            "AND a.school.id = :schoolId " +
            "AND a.appointmentDate BETWEEN :periodStart AND :periodEnd " +
            "GROUP BY a.school.id, a.school.name")
    AppointmentStatisticsDto getAppointmentStatistics(@Param("schoolId") Long schoolId,
                                                      @Param("periodStart") LocalDate periodStart,
                                                      @Param("periodEnd") LocalDate periodEnd);

    // Availability queries
    @Query("SELECT new com.genixo.education.search.dto.appointment.AppointmentAvailabilityDto(" +
            "s.id, s.name, :date, " +
            "null, " +
            "CAST(COUNT(DISTINCT slot.id) AS int), " +
            "CAST(COUNT(a.id) AS int), " +
            "CAST(COUNT(DISTINCT slot.id) - COUNT(a.id) AS int), " +
            "'') " +
            "FROM School s " +
            "LEFT JOIN AppointmentSlot slot ON slot.school.id = s.id AND slot.isActive = true " +
            "LEFT JOIN Appointment a ON a.appointmentSlot.id = slot.id " +
            "    AND a.appointmentDate = :date AND a.isActive = true " +
            "    AND a.status IN ('CONFIRMED', 'PENDING', 'IN_PROGRESS') " +
            "WHERE s.id = :schoolId AND s.isActive = true AND s.campus.isSubscribed = true " +
            "GROUP BY s.id, s.name")
    List<AppointmentAvailabilityDto> getPublicAvailability(@Param("schoolId") Long schoolId,
                                                           @Param("date") LocalDate date);

    // Calendar queries
    @Query("SELECT new com.genixo.education.search.dto.appointment.AppointmentCalendarDto(" +
            "a.appointmentDate, " +
            "null, " + // Events - would be populated separately
            "CAST(COUNT(a) AS int), " +
            "0, " + // Available slots - calculated separately
            "false) " + // Has conflicts - calculated separately
            "FROM Appointment a " +
            "WHERE a.school.id = :schoolId " +
            "AND a.appointmentDate BETWEEN :startDate AND :endDate " +
            "AND a.isActive = true " +
            "GROUP BY a.appointmentDate")
    List<AppointmentCalendarDto> getAppointmentCalendar(@Param("schoolId") Long schoolId,
                                                        @Param("startDate") LocalDate startDate,
                                                        @Param("endDate") LocalDate endDate);

    // Staff performance
    @Query("SELECT new com.genixo.education.search.dto.appointment.StaffPerformanceDto(" +
            "su.id, " +
            "CONCAT(su.firstName, ' ', su.lastName), " +
            "'Staff', " +
            "CAST(COUNT(a) AS int), " +
            "CAST(SUM(CASE WHEN a.status = 'COMPLETED' THEN 1 ELSE 0 END) AS int), " +
            "CAST(SUM(CASE WHEN a.status = 'CANCELLED' THEN 1 ELSE 0 END) AS int), " +
            "CAST(SUM(CASE WHEN a.status = 'NO_SHOW' THEN 1 ELSE 0 END) AS int), " +
            "CASE WHEN COUNT(a) > 0 THEN CAST(SUM(CASE WHEN a.status = 'COMPLETED' THEN 1 ELSE 0 END) AS double) / COUNT(a) * 100 ELSE 0.0 END, " +
            "CASE WHEN COUNT(a) > 0 THEN CAST(SUM(CASE WHEN a.status = 'CANCELLED' THEN 1 ELSE 0 END) AS double) / COUNT(a) * 100 ELSE 0.0 END, " +
            "CASE WHEN COUNT(a) > 0 THEN CAST(SUM(CASE WHEN a.status = 'NO_SHOW' THEN 1 ELSE 0 END) AS double) / COUNT(a) * 100 ELSE 0.0 END, " +
            "CAST(SUM(CASE WHEN a.outcome = 'ENROLLED' THEN 1 ELSE 0 END) AS int), " +
            "CASE WHEN SUM(CASE WHEN a.status = 'COMPLETED' THEN 1 ELSE 0 END) > 0 THEN " +
            "     CAST(SUM(CASE WHEN a.outcome = 'ENROLLED' THEN 1 ELSE 0 END) AS double) / SUM(CASE WHEN a.status = 'COMPLETED' THEN 1 ELSE 0 END) * 100 " +
            "     ELSE 0.0 END, " +
            "AVG(COALESCE(a.enrollmentLikelihood, 0)), " +
            "30.0, " +
            "0.0, " +
            "'', " +
            "null, " +
            "null) " +
            "FROM Appointment a " +
            "JOIN a.staffUser su " +
            "WHERE a.school.id = :schoolId " +
            "AND a.appointmentDate BETWEEN :periodStart AND :periodEnd " +
            "AND a.isActive = true " +
            "GROUP BY su.id, su.firstName, su.lastName " +
            "ORDER BY COUNT(a) DESC")
    List<StaffPerformanceDto> getStaffPerformance(@Param("schoolId") Long schoolId,
                                                  @Param("periodStart") LocalDate periodStart,
                                                  @Param("periodEnd") LocalDate periodEnd);

    // Outcome analysis
    @Query("SELECT new com.genixo.education.search.dto.appointment.OutcomeAnalysisDto(" +
            "a.outcome, " +
            "'', " +
            "CAST(COUNT(a) AS int), " +
            "0.0, " +
            "AVG(COALESCE(a.enrollmentLikelihood, 0)), " +
            "null, " +
            "null) " +
            "FROM Appointment a " +
            "WHERE a.school.id = :schoolId " +
            "AND a.appointmentDate BETWEEN :periodStart AND :periodEnd " +
            "AND a.isActive = true " +
            "AND a.outcome IS NOT NULL " +
            "GROUP BY a.outcome " +
            "ORDER BY COUNT(a) DESC")
    List<OutcomeAnalysisDto> getOutcomeAnalysis(@Param("schoolId") Long schoolId,
                                                @Param("periodStart") LocalDate periodStart,
                                                @Param("periodEnd") LocalDate periodEnd);

    // Time slot analysis
    @Query("SELECT new com.genixo.education.search.dto.appointment.TimeSlotAnalysisDto(" +
            "CONCAT(CAST(slot.startTime AS string), '-', CAST(slot.endTime AS string)), " +
            "slot.dayOfWeek, " +
            "'', " + // Day of week name - set in service
            "CAST(slot.capacity AS int), " +
            "CAST(COUNT(a) AS int), " +
            "CAST(SUM(CASE WHEN a.status = 'COMPLETED' THEN 1 ELSE 0 END) AS int), " +
            "CASE WHEN slot.capacity > 0 THEN CAST(COUNT(a) AS double) / slot.capacity * 100 ELSE 0.0 END, " +
            "CASE WHEN COUNT(a) > 0 THEN CAST(SUM(CASE WHEN a.status = 'COMPLETED' THEN 1 ELSE 0 END) AS double) / COUNT(a) * 100 ELSE 0.0 END, " +
            "CASE WHEN SUM(CASE WHEN a.status = 'COMPLETED' THEN 1 ELSE 0 END) > 0 THEN " +
            "     CAST(SUM(CASE WHEN a.outcome = 'ENROLLED' THEN 1 ELSE 0 END) AS double) / SUM(CASE WHEN a.status = 'COMPLETED' THEN 1 ELSE 0 END) * 100 " +
            "     ELSE 0.0 END, " +
            "false, " + // Is popular - calculated in service
            "false) " + // Is recommended - calculated in service
            "FROM AppointmentSlot slot " +
            "LEFT JOIN Appointment a ON a.appointmentSlot.id = slot.id " +
            "    AND a.appointmentDate BETWEEN :periodStart AND :periodEnd " +
            "    AND a.isActive = true " +
            "WHERE slot.school.id = :schoolId " +
            "AND slot.isActive = true " +
            "GROUP BY slot.id, slot.startTime, slot.endTime, slot.dayOfWeek, slot.capacity " +
            "ORDER BY slot.dayOfWeek, slot.startTime")
    List<TimeSlotAnalysisDto> getTimeSlotAnalysis(@Param("schoolId") Long schoolId,
                                                  @Param("periodStart") LocalDate periodStart,
                                                  @Param("periodEnd") LocalDate periodEnd);

    // Daily statistics
    @Query("SELECT new com.genixo.education.search.dto.appointment.DailyAppointmentStatsDto(" +
            "a.appointmentDate, " +
            "CAST(COUNT(a) AS int), " +
            "CAST(SUM(CASE WHEN a.status = 'COMPLETED' THEN 1 ELSE 0 END) AS int), " +
            "CAST(SUM(CASE WHEN a.status = 'CANCELLED' THEN 1 ELSE 0 END) AS int), " +
            "CAST(SUM(CASE WHEN a.status = 'NO_SHOW' THEN 1 ELSE 0 END) AS int), " +
            "CASE WHEN COUNT(a) > 0 THEN CAST(SUM(CASE WHEN a.status = 'COMPLETED' THEN 1 ELSE 0 END) AS double) / COUNT(a) * 100 ELSE 0.0 END, " +
            "CAST(SUM(CASE WHEN a.outcome = 'ENROLLED' THEN 1 ELSE 0 END) AS int)) " +
            "FROM Appointment a " +
            "WHERE a.school.id = :schoolId " +
            "AND a.appointmentDate BETWEEN :periodStart AND :periodEnd " +
            "AND a.isActive = true " +
            "GROUP BY a.appointmentDate " +
            "ORDER BY a.appointmentDate")
    List<DailyAppointmentStatsDto> getDailyAppointmentStats(@Param("schoolId") Long schoolId,
                                                            @Param("periodStart") LocalDate periodStart,
                                                            @Param("periodEnd") LocalDate periodEnd);

    // Monthly statistics
    @Query("SELECT new com.genixo.education.search.dto.appointment.MonthlyAppointmentStatsDto(" +
            "YEAR(a.appointmentDate), " +
            "MONTH(a.appointmentDate), " +
            "'', " + // Month name - set in service
            "CAST(COUNT(a) AS int), " +
            "CAST(SUM(CASE WHEN a.status = 'COMPLETED' THEN 1 ELSE 0 END) AS int), " +
            "CASE WHEN COUNT(a) > 0 THEN CAST(SUM(CASE WHEN a.status = 'COMPLETED' THEN 1 ELSE 0 END) AS double) / COUNT(a) * 100 ELSE 0.0 END, " +
            "CAST(SUM(CASE WHEN a.outcome = 'ENROLLED' THEN 1 ELSE 0 END) AS int), " +
            "CASE WHEN SUM(CASE WHEN a.status = 'COMPLETED' THEN 1 ELSE 0 END) > 0 THEN " +
            "     CAST(SUM(CASE WHEN a.outcome = 'ENROLLED' THEN 1 ELSE 0 END) AS double) / SUM(CASE WHEN a.status = 'COMPLETED' THEN 1 ELSE 0 END) * 100 " +
            "     ELSE 0.0 END) " +
            "FROM Appointment a " +
            "WHERE a.school.id = :schoolId " +
            "AND a.appointmentDate BETWEEN :periodStart AND :periodEnd " +
            "AND a.isActive = true " +
            "GROUP BY YEAR(a.appointmentDate), MONTH(a.appointmentDate) " +
            "ORDER BY YEAR(a.appointmentDate), MONTH(a.appointmentDate)")
    List<MonthlyAppointmentStatsDto> getMonthlyAppointmentStats(@Param("schoolId") Long schoolId,
                                                                @Param("periodStart") LocalDate periodStart,
                                                                @Param("periodEnd") LocalDate periodEnd);

    // Detailed appointment list for reports
    @Query("SELECT new com.genixo.education.search.dto.appointment.AppointmentSummaryDto(" +
            "a.id, a.appointmentNumber, a.school.name, a.parentName, a.studentName, " +
            "a.appointmentDate, a.startTime, a.endTime, a.status, a.appointmentType, " +
            "a.location, a.isOnline, " +
            "COALESCE(CONCAT(a.staffUser.firstName, ' ', a.staffUser.lastName), ''), " +
            "a.outcome, a.followUpRequired, " +
            "'', '', '') " + // Display fields - set in service
            "FROM Appointment a " +
            "WHERE a.school.id = :schoolId " +
            "AND a.appointmentDate BETWEEN :periodStart AND :periodEnd " +
            "AND a.isActive = true " +
            "ORDER BY a.appointmentDate DESC, a.startTime DESC")
    List<AppointmentSummaryDto> getDetailedAppointmentList(@Param("schoolId") Long schoolId,
                                                           @Param("periodStart") LocalDate periodStart,
                                                           @Param("periodEnd") LocalDate periodEnd);

    // Metrics queries  ceyhun
    /*
    @Query("SELECT new com.genixo.education.search.dto.appointment.AppointmentMetricsDto(" +
            ":metricType, 'Total Appointments', 'COUNT', " +
            "CAST(COUNT(a) AS double), " +
            "0.0, 0.0, 0.0, '', '', '', '', 0.0, '') " +
            "FROM Appointment a " +
            "WHERE a.school.id = :schoolId " +
            "AND a.appointmentDate BETWEEN :periodStart AND :periodEnd " +
            "AND a.isActive = true")
    List<AppointmentMetricsDto> getAppointmentMetrics(@Param("schoolId") Long schoolId,
                                                      @Param("periodStart") LocalDate periodStart,
                                                      @Param("periodEnd") LocalDate periodEnd,
                                                      @Param("metricType") String metricType);

     */

    // Reminder queries
    @Query("SELECT a FROM Appointment a " +
            "WHERE a.isActive = true " +
            "AND a.status IN ('CONFIRMED', 'PENDING') " +
            "AND a.appointmentDate = :reminderDate " +
            "AND (a.reminderSentAt IS NULL OR a.reminderSentAt < :lastReminderCutoff)")
    List<Appointment> findAppointmentsNeedingReminder(@Param("reminderDate") LocalDateTime reminderDate,
                                                      @Param("lastReminderCutoff") LocalDateTime lastReminderCutoff);

    @Query("SELECT a FROM Appointment a " +
            "WHERE a.isActive = true " +
            "AND a.status IN ('CONFIRMED', 'PENDING') " +
            "AND CONCAT(a.appointmentDate, ' ', a.startTime) = :reminderTime " +
            "AND (a.reminderSentAt IS NULL)")
    List<Appointment> findAppointmentsNeedingReminder(@Param("reminderTime") LocalDateTime reminderTime);

    // Maintenance queries
    @Modifying
    @Query("UPDATE Appointment a SET a.isActive = false WHERE a.appointmentDate < :cutoffDate")
    int archiveOldAppointments(@Param("cutoffDate") LocalDate cutoffDate);

    @Query("SELECT s.id FROM School s WHERE s.isActive = true")
    List<Long> findAllActiveSchoolIds();

    @Query("SELECT s.id FROM School s WHERE s.campus.id = :campusId AND s.isActive = true")
    List<Long> findIdsByCampusId(@Param("campusId") Long campusId);

    @Query("SELECT s.id FROM School s WHERE s.campus.brand.id = :brandId AND s.isActive = true")
    List<Long> findIdsByBrandId(@Param("brandId") Long brandId);

    @Query("SELECT slot.dayOfWeek, slot.startTime, slot.endTime, slot.capacity, " +
            "(SELECT COUNT(app) FROM Appointment app " +
            "WHERE app.appointmentSlot.id = slot.id " +
            "AND app.appointmentDate BETWEEN :startDate AND :endDate " +
            "AND app.status IN ('PENDING', 'CONFIRMED', 'APPROVED') " +
            "AND app.isActive = true) " +
            "FROM AppointmentSlot slot " +
            "WHERE slot.school.id = :schoolId " +
            "AND slot.isActive = true " +
            "ORDER BY slot.dayOfWeek, slot.startTime")
    List<Object[]> getAvailabilityBetweenDatesRaw(@Param("schoolId") Long schoolId,
                                                  @Param("startDate") LocalDate startDate,
                                                  @Param("endDate") LocalDate endDate);


    // AvailableSlotDto için query (DTO property'lerine uygun)
    @Query("SELECT slot.id, slot.startTime, slot.endTime, slot.durationMinutes, " +
            "slot.appointmentType, slot.location, slot.onlineMeetingAvailable, " +
            "CASE WHEN slot.staffUser IS NOT NULL THEN " +
            "CONCAT(slot.staffUser.firstName, ' ', slot.staffUser.lastName) ELSE null END, " +
            "slot.capacity, slot.requiresApproval " +
            "FROM AppointmentSlot slot " +
            "WHERE slot.school.id = :schoolId " +
            "AND slot.isActive = true " +
            "AND slot.dayOfWeek = :dayOfWeek " +
            "ORDER BY slot.startTime")
    List<Object[]> getAvailableSlotsForDateRaw(@Param("schoolId") Long schoolId, @Param("dayOfWeek") DayOfWeek dayOfWeek);


    // Okul için tüm aktif slot'ları getirir (optimize edilmiş versiyon için)
    @Query("SELECT slot.dayOfWeek, slot.id, slot.startTime, slot.endTime, slot.durationMinutes, " +
            "slot.appointmentType, slot.location, slot.onlineMeetingAvailable, " +
            "COALESCE(slot.staffUser.email, 'Genel'), slot.capacity, slot.requiresApproval " +
            "FROM AppointmentSlot slot " +
            "WHERE slot.school.id = :schoolId " +
            "AND slot.isActive = true " +
            "AND (slot.validFrom IS NULL OR slot.validFrom <= :currentDate) " +
            "AND (slot.validUntil IS NULL OR slot.validUntil >= :currentDate) " +
            "ORDER BY slot.dayOfWeek, slot.startTime")
    List<Object[]> getAllActiveSlotsForSchool(@Param("schoolId") Long schoolId,
                                              @Param("currentDate") LocalDate currentDate);

    // Tarih aralığındaki rezervasyon sayılarını getirir (optimize edilmiş)
    @Query("SELECT app.appointmentDate, app.appointmentSlot.id, COUNT(app) " +
            "FROM Appointment app " +
            "WHERE app.school.id = :schoolId " +
            "AND app.appointmentDate BETWEEN :startDate AND :endDate " +
            "AND app.status IN ('PENDING', 'CONFIRMED', 'APPROVED') " +
            "AND app.isActive = true " +
            "GROUP BY app.appointmentDate, app.appointmentSlot.id")
    List<Object[]> getBookedCountsByDateRange(@Param("schoolId") Long schoolId,
                                              @Param("startDate") LocalDate startDate,
                                              @Param("endDate") LocalDate endDate);

    // Excluded dates kontrolü için slot'ları getirir
    @Query("SELECT slot.id, slot.excludedDates " +
            "FROM AppointmentSlot slot " +
            "WHERE slot.school.id = :schoolId " +
            "AND slot.isActive = true " +
            "AND slot.excludedDates IS NOT NULL")
    List<Object[]> getSlotsWithExcludedDates(@Param("schoolId") Long schoolId);



    // Belirli tarih için rezervasyon sayılarını getirir (mevcut metodunuz)
    @Query("SELECT app.appointmentSlot.id, COUNT(app) " +
            "FROM Appointment app " +
            "WHERE app.school.id = :schoolId " +
            "AND app.appointmentDate = :date " +
            "AND app.status IN ('PENDING', 'CONFIRMED', 'APPROVED') " +
            "AND app.isActive = true " +
            "GROUP BY app.appointmentSlot.id")
    List<Object[]> getBookedCountsBySlot(@Param("schoolId") Long schoolId,
                                         @Param("date") LocalDate date);

    // Günlük toplam slot sayısını getirir
    @Query("SELECT COUNT(slot) " +
            "FROM AppointmentSlot slot " +
            "WHERE slot.school.id = :schoolId " +
            "AND slot.dayOfWeek = :dayOfWeek " +
            "AND slot.isActive = true " +
            "AND (slot.validFrom IS NULL OR slot.validFrom <= CURRENT_DATE) " +
            "AND (slot.validUntil IS NULL OR slot.validUntil >= CURRENT_DATE)")
    Integer getTotalSlotsForDay(@Param("schoolId") Long schoolId,
                                @Param("dayOfWeek") DayOfWeek dayOfWeek);

    // Günlük rezerve edilmiş slot sayısını getirir
    @Query("SELECT COUNT(app) " +
            "FROM Appointment app " +
            "WHERE app.school.id = :schoolId " +
            "AND app.appointmentDate = :date " +
            "AND app.status IN ('PENDING', 'CONFIRMED', 'APPROVED') " +
            "AND app.isActive = true")
    Integer getBookedSlotsForDate(@Param("schoolId") Long schoolId,
                                  @Param("date") LocalDate date);


}