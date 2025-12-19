package com.genixo.education.search.service.search;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

/**
 * School Data Change Listener
 * Okul verileri değiştiğinde materialized view'i günceller
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class SchoolDataChangeListener {

    private final MaterializedViewRefreshService refreshService;

    /**
     * Okul oluşturulduğunda
     */
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onSchoolCreated(SchoolCreatedEvent event) {
        log.info("School created: {} - Refreshing materialized view", event.getSchoolId());
        refreshService.refreshSchoolSearchViewAsync();
    }

    /**
     * Okul güncellendiğinde
     */
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onSchoolUpdated(SchoolUpdatedEvent event) {
        log.info("School updated: {} - Refreshing materialized view", event.getSchoolId());
        refreshService.refreshSchoolSearchViewAsync();
    }

    /**
     * Okul silindiğinde
     */
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onSchoolDeleted(SchoolDeletedEvent event) {
        log.info("School deleted: {} - Refreshing materialized view", event.getSchoolId());
        refreshService.refreshSchoolSearchViewAsync();
    }

    /**
     * Kampüs güncellendiğinde
     */
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onCampusUpdated(CampusUpdatedEvent event) {
        log.info("Campus updated: {} - Refreshing materialized view", event.getCampusId());
        refreshService.refreshSchoolSearchViewAsync();
    }

    /**
     * Özellik güncellendiğinde
     */
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onPropertyUpdated(PropertyUpdatedEvent event) {
        log.info("Property updated for school: {} - Refreshing materialized view", event.getSchoolId());
        refreshService.refreshSchoolSearchViewAsync();
    }

    // ============ EVENT CLASSES ============

    public static class SchoolCreatedEvent {
        private final Long schoolId;

        public SchoolCreatedEvent(Long schoolId) {
            this.schoolId = schoolId;
        }

        public Long getSchoolId() {
            return schoolId;
        }
    }

    public static class SchoolUpdatedEvent {
        private final Long schoolId;

        public SchoolUpdatedEvent(Long schoolId) {
            this.schoolId = schoolId;
        }

        public Long getSchoolId() {
            return schoolId;
        }
    }

    public static class SchoolDeletedEvent {
        private final Long schoolId;

        public SchoolDeletedEvent(Long schoolId) {
            this.schoolId = schoolId;
        }

        public Long getSchoolId() {
            return schoolId;
        }
    }

    public static class CampusUpdatedEvent {
        private final Long campusId;

        public CampusUpdatedEvent(Long campusId) {
            this.campusId = campusId;
        }

        public Long getCampusId() {
            return campusId;
        }
    }

    public static class PropertyUpdatedEvent {
        private final Long schoolId;

        public PropertyUpdatedEvent(Long schoolId) {
            this.schoolId = schoolId;
        }

        public Long getSchoolId() {
            return schoolId;
        }
    }
}