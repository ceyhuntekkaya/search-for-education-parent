package com.genixo.education.search.repository.content;

import com.genixo.education.search.dto.content.MessageStatisticsDto;
import com.genixo.education.search.entity.content.Message;
import com.genixo.education.search.enumaration.MessagePriority;
import com.genixo.education.search.enumaration.MessageStatus;
import com.genixo.education.search.enumaration.MessageType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface MessageRepository extends JpaRepository<Message, Long> {

    @Query("SELECT m FROM Message m WHERE m.isActive = true AND m.id = :id")
    Optional<Message> findByIdAndIsActiveTrue(@Param("id") Long id);

    @Query("SELECT m FROM Message m WHERE m.referenceNumber = :referenceNumber AND m.isActive = true")
    Optional<Message> findByReferenceNumberAndIsActiveTrue(@Param("referenceNumber") String referenceNumber);

    @Query("SELECT m FROM Message m WHERE m.school.id = :schoolId AND m.isActive = true " +
            "ORDER BY m.createdAt DESC")
    Page<Message> findBySchoolIdAndIsActiveTrue(@Param("schoolId") Long schoolId, Pageable pageable);


    @Query("SELECT m FROM Message m WHERE m.school.id = :schoolId AND m.isActive = true " +
            "ORDER BY m.createdAt DESC")
    List<Message> findBySchoolIdAndIsActiveTrueList(@Param("schoolId") Long schoolId);




    @Query("SELECT m FROM Message m WHERE m.assignedToUser.id = :userId AND m.isActive = true " +
            "ORDER BY m.createdAt DESC")
    Page<Message> findByAssignedToUserIdAndIsActiveTrue(@Param("userId") Long userId, Pageable pageable);

    @Query("SELECT m FROM Message m WHERE m.status = :status AND m.isActive = true " +
            "ORDER BY m.createdAt DESC")
    Page<Message> findByStatusAndIsActiveTrue(@Param("status") MessageStatus status, Pageable pageable);

    // Complex search query
    @Query("SELECT DISTINCT m FROM Message m " +
            "WHERE m.isActive = true " +
            "AND (:searchTerm IS NULL OR " +
            "    LOWER(m.senderName) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
            "    LOWER(m.senderEmail) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
            "    LOWER(m.subject) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
            "    LOWER(m.content) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
            "    LOWER(m.referenceNumber) LIKE LOWER(CONCAT('%', :searchTerm, '%'))) " +
            "AND (:schoolId IS NULL OR m.school.id = :schoolId) " +
            "AND (:accessibleSchoolIds IS NULL OR m.school.id IN :accessibleSchoolIds) " +
            "AND (:assignedToUserId IS NULL OR m.assignedToUser.id = :assignedToUserId) " +
            "AND (:messageType IS NULL OR m.messageType = :messageType) " +
            "AND (:priority IS NULL OR m.priority = :priority) " +
            "AND (:status IS NULL OR m.status = :status) " +
            "AND (:followUpRequired IS NULL OR m.followUpRequired = :followUpRequired) " +
            "AND (:hasAttachments IS NULL OR m.hasAttachments = :hasAttachments) " +
            "AND (:createdAfter IS NULL OR m.createdAt >= :createdAfter) " +
            "AND (:createdBefore IS NULL OR m.createdAt <= :createdBefore) " +
            "AND (:tags IS NULL OR LOWER(m.tags) LIKE LOWER(CONCAT('%', :tags, '%')))")
    Page<Message> searchMessages(
            @Param("searchTerm") String searchTerm,
            @Param("schoolId") Long schoolId,
            @Param("accessibleSchoolIds") List<Long> accessibleSchoolIds,
            @Param("assignedToUserId") Long assignedToUserId,
            @Param("messageType") MessageType messageType,
            @Param("priority") MessagePriority priority,
            @Param("status") MessageStatus status,
            @Param("followUpRequired") Boolean followUpRequired,
            @Param("hasAttachments") Boolean hasAttachments,
            @Param("createdAfter") LocalDateTime createdAfter,
            @Param("createdBefore") LocalDateTime createdBefore,
            @Param("tags") String tags,
            Pageable pageable);

    // Message Statistics Query ceyhun
    /*
    @Query("""
            SELECT new com.genixo.education.search.dto.content.MessageStatisticsDto(
              COALESCE(COUNT(m), 0L),
              COALESCE(SUM(CASE WHEN m.status = com.genixo.education.search.enumaration.MessageStatus.NEW THEN 1L ELSE 0L END), 0L),
              COALESCE(SUM(CASE WHEN m.status IN (com.genixo.education.search.enumaration.MessageStatus.IN_PROGRESS, com.genixo.education.search.enumaration.MessageStatus.WAITING_RESPONSE) THEN 1L ELSE 0L END), 0L),
              COALESCE(SUM(CASE WHEN m.status IN (com.genixo.education.search.enumaration.MessageStatus.RESOLVED, com.genixo.education.search.enumaration.MessageStatus.CLOSED) THEN 1L ELSE 0L END), 0L),
              COALESCE(SUM(CASE WHEN m.followUpRequired = true AND m.followUpDate < CURRENT_TIMESTAMP THEN 1L ELSE 0L END), 0L),
            
              COALESCE(AVG(m.responseTimeHours), 0.0),
              COALESCE(AVG(m.resolutionTimeHours), 0.0),
              COALESCE(AVG(m.satisfactionRating), 0.0),
            
              COALESCE(SUM(CASE WHEN m.followUpRequired = true THEN 1L ELSE 0L END), 0L),
            
              COALESCE(SUM(CASE WHEN m.messageType IN (com.genixo.education.search.enumaration.MessageType.GENERAL_INQUIRY, com.genixo.education.search.enumaration.MessageType.ENROLLMENT_INQUIRY) THEN 1L ELSE 0L END), 0L),
              COALESCE(SUM(CASE WHEN m.messageType = com.genixo.education.search.enumaration.MessageType.COMPLAINT THEN 1L ELSE 0L END), 0L),
              COALESCE(SUM(CASE WHEN m.messageType = com.genixo.education.search.enumaration.MessageType.APPOINTMENT_REQUEST THEN 1L ELSE 0L END), 0L),
              COALESCE(SUM(CASE WHEN m.messageType = com.genixo.education.search.enumaration.MessageType.CALLBACK_REQUEST THEN 1L ELSE 0L END), 0L),
            
              COALESCE(SUM(CASE WHEN m.priority IN (com.genixo.education.search.enumaration.MessagePriority.URGENT, com.genixo.education.search.enumaration.MessagePriority.CRITICAL) THEN 1L ELSE 0L END), 0L),
              COALESCE(SUM(CASE WHEN m.priority = com.genixo.education.search.enumaration.MessagePriority.HIGH THEN 1L ELSE 0L END), 0L),
              COALESCE(SUM(CASE WHEN m.priority = com.genixo.education.search.enumaration.MessagePriority.NORMAL THEN 1L ELSE 0L END), 0L),
              COALESCE(SUM(CASE WHEN m.priority = com.genixo.education.search.enumaration.MessagePriority.LOW THEN 1L ELSE 0L END), 0L)
            )
            FROM Message m
            WHERE m.school.id IN :schoolIds AND m.isActive = true
            """)
    MessageStatisticsDto getMessageStatistics(@Param("schoolIds") List<Long> schoolIds);

     */

    @Query("SELECT m FROM Message m WHERE m.status = 'NEW' AND m.isActive = true " +
            "ORDER BY m.priority DESC, m.createdAt ASC")
    List<Message> findNewMessagesOrderByPriority();

    @Query("SELECT m FROM Message m WHERE m.followUpRequired = true AND m.followUpDate <= :now " +
            "AND m.status NOT IN ('RESOLVED', 'CLOSED') AND m.isActive = true")
    List<Message> findMessagesRequiringFollowUp(@Param("now") LocalDateTime now);

    @Query("SELECT m FROM Message m WHERE m.status IN ('NEW', 'IN_PROGRESS') " +
            "AND m.createdAt <= :threshold AND m.isActive = true")
    List<Message> findOverdueMessages(@Param("threshold") LocalDateTime threshold);

    @Query("SELECT COUNT(m) FROM Message m WHERE m.school.id = :schoolId " +
            "AND m.createdAt >= :startDate AND m.isActive = true")
    long countMessagesBySchoolAndDateAfter(@Param("schoolId") Long schoolId, @Param("startDate") LocalDateTime startDate);

    @Query("SELECT m FROM Message m WHERE m.senderEmail = :email AND m.isActive = true " +
            "ORDER BY m.createdAt DESC")
    List<Message> findBySenderEmailAndIsActiveTrue(@Param("email") String email);

    @Query("SELECT m FROM Message m WHERE m.requestAppointment = true AND m.status = 'NEW' AND m.isActive = true")
    List<Message> findAppointmentRequests();

    @Query("SELECT m FROM Message m WHERE m.requestCallback = true AND m.status IN ('NEW', 'IN_PROGRESS') AND m.isActive = true")
    List<Message> findCallbackRequests();

    @Query("SELECT DISTINCT m.tags FROM Message m WHERE m.isActive = true AND m.tags IS NOT NULL")
    List<String> findDistinctTags();

    @Query("SELECT COUNT(m) FROM Message m WHERE m.school.id = :schoolId AND m.status = :status " +
            "AND m.createdAt >= :date AND m.isActive = true")
    long countBySchoolIdAndStatusLast30Days(@Param("schoolId") Long schoolId, @Param("status") MessageStatus status, @Param("date") LocalDateTime date);

    @Query("SELECT AVG(m.responseTimeHours) FROM Message m WHERE m.school.id = :schoolId " +
            "AND m.responseTimeHours IS NOT NULL AND m.isActive = true")
    Double getAverageResponseTimeBySchoolId(@Param("schoolId") Long schoolId);

    @Query("SELECT AVG(m.resolutionTimeHours) FROM Message m WHERE m.school.id = :schoolId " +
            "AND m.resolutionTimeHours IS NOT NULL AND m.isActive = true")
    Double getAverageResolutionTimeBySchoolId(@Param("schoolId") Long schoolId);

    @Query("SELECT AVG(CAST(m.satisfactionRating AS DOUBLE)) FROM Message m WHERE m.school.id = :schoolId " +
            "AND m.satisfactionRating IS NOT NULL AND m.isActive = true")
    Double getAverageSatisfactionRatingBySchoolId(@Param("schoolId") Long schoolId);

    @Query("SELECT m FROM Message m WHERE m.senderUser.id = :userId OR m.assignedToUser.id = :userId")
    List<Message> findByUser(@Param("userId") Long userId);

    @Query("SELECT m FROM Message m WHERE m.school.id = :schoolId")
    List<Message> findBySchool(@Param("schoolId") Long schoolId);
}
