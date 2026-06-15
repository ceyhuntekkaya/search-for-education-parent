package com.genixo.education.search.service.appointment;

import com.genixo.education.search.common.exception.BusinessException;
import com.genixo.education.search.dto.appointment.BulkAppointmentOperationDto;
import com.genixo.education.search.enumaration.AppointmentStatus;
import com.genixo.education.search.enumaration.ParentFollowUpOutcome;
import org.springframework.util.StringUtils;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.EnumSet;
import java.util.Set;

public final class ParentFollowUpOutcomeValidator {

    private static final Set<ParentFollowUpOutcome> FOLLOW_UP_SCHEDULE_REQUIRED = EnumSet.of(
            ParentFollowUpOutcome.TO_FOLLOW_UP,
            ParentFollowUpOutcome.APPOINTMENT_POSTPONED,
            ParentFollowUpOutcome.WAITING_FOR_INFO,
            ParentFollowUpOutcome.DID_NOT_ATTEND
    );

    private static final Set<ParentFollowUpOutcome> NO_SALE_REASON_REQUIRED = EnumSet.of(
            ParentFollowUpOutcome.TO_FOLLOW_UP,
            ParentFollowUpOutcome.WONT_FOLLOW_UP
    );

    private ParentFollowUpOutcomeValidator() {
    }

    public static void validateBulkRequest(BulkAppointmentOperationDto bulkDto) {
        if (bulkDto.getParentFollowUpOutcome() == null) {
            return;
        }

        if (!"UPDATE_STATUS".equalsIgnoreCase(bulkDto.getOperation())) {
            throw new BusinessException("parentFollowUpOutcome is only supported with operation UPDATE_STATUS");
        }

        if (bulkDto.getAppointmentIds() == null || bulkDto.getAppointmentIds().isEmpty()) {
            throw new BusinessException("appointmentIds is required");
        }

        ParentFollowUpOutcome outcome = bulkDto.getParentFollowUpOutcome();
        validateRequiredFields(outcome, bulkDto);
        validateStatusConsistency(outcome, bulkDto.getNewStatus());
    }

    public static void validateRequiredFields(ParentFollowUpOutcome outcome, BulkAppointmentOperationDto bulkDto) {
        if (FOLLOW_UP_SCHEDULE_REQUIRED.contains(outcome)) {
            requireDate(outcome, bulkDto.getFollowUpDate());
            requireTime(outcome, bulkDto.getFollowUpTime());
            requireText(outcome, "outcomeNotes", bulkDto.getOutcomeNotes());
        }

        if (NO_SALE_REASON_REQUIRED.contains(outcome)) {
            requireText(outcome, "noSaleReason", bulkDto.getNoSaleReason());
        }
    }

    public static void validateStatusConsistency(ParentFollowUpOutcome outcome, AppointmentStatus requestedStatus) {
        AppointmentStatus effectiveStatus = requestedStatus != null ? requestedStatus : resolveDefaultStatus(outcome);

        if (outcome == ParentFollowUpOutcome.ENROLLED && effectiveStatus != AppointmentStatus.COMPLETED) {
            throw new BusinessException("ENROLLED outcome requires newStatus COMPLETED");
        }

        if (outcome == ParentFollowUpOutcome.DID_NOT_ATTEND && effectiveStatus != AppointmentStatus.NO_SHOW) {
            throw new BusinessException("DID_NOT_ATTEND outcome requires newStatus NO_SHOW");
        }
    }

    public static AppointmentStatus resolveDefaultStatus(ParentFollowUpOutcome outcome) {
        return switch (outcome) {
            case DID_NOT_ATTEND -> AppointmentStatus.NO_SHOW;
            case ENROLLED, TO_FOLLOW_UP, APPOINTMENT_POSTPONED, WAITING_FOR_INFO, WONT_FOLLOW_UP ->
                    AppointmentStatus.COMPLETED;
        };
    }

    public static boolean resolveFollowUpRequired(ParentFollowUpOutcome outcome, Boolean requestedValue) {
        if (requestedValue != null) {
            return requestedValue;
        }
        return FOLLOW_UP_SCHEDULE_REQUIRED.contains(outcome);
    }

    public static String buildSummaryMessage(BulkAppointmentOperationDto bulkDto) {
        if (bulkDto.getParentFollowUpOutcome() == null) {
            return null;
        }

        StringBuilder message = new StringBuilder();
        message.append("Sonuç: ").append(formatOutcomeLabel(bulkDto.getParentFollowUpOutcome()));

        if (StringUtils.hasText(bulkDto.getNoSaleReason())) {
            message.append("\nSatış Olmama Sebebi: ").append(bulkDto.getNoSaleReason().trim());
        }

        if (bulkDto.getFollowUpDate() != null) {
            message.append("\nArama Tarihi: ").append(bulkDto.getFollowUpDate());
            if (bulkDto.getFollowUpTime() != null) {
                message.append(" ").append(bulkDto.getFollowUpTime());
            }
        }

        if (StringUtils.hasText(bulkDto.getOutcomeNotes())) {
            message.append("\nNot: ").append(bulkDto.getOutcomeNotes().trim());
        }

        return message.toString();
    }

    private static String formatOutcomeLabel(ParentFollowUpOutcome outcome) {
        return switch (outcome) {
            case ENROLLED -> "Kayıt Oldu";
            case TO_FOLLOW_UP -> "Takip Edilecek";
            case APPOINTMENT_POSTPONED -> "Randevu Erteleme";
            case WAITING_FOR_INFO -> "Bilgi Bekliyor";
            case WONT_FOLLOW_UP -> "Takip Edilmeyecek";
            case DID_NOT_ATTEND -> "Görüşmeye Gelmedi";
        };
    }

    private static void requireDate(ParentFollowUpOutcome outcome, LocalDate followUpDate) {
        if (followUpDate == null) {
            throw new BusinessException(fieldRequiredMessage(outcome, "followUpDate"));
        }
    }

    private static void requireTime(ParentFollowUpOutcome outcome, LocalTime followUpTime) {
        if (followUpTime == null) {
            throw new BusinessException(fieldRequiredMessage(outcome, "followUpTime"));
        }
    }

    private static void requireText(ParentFollowUpOutcome outcome, String fieldName, String value) {
        if (!StringUtils.hasText(value)) {
            throw new BusinessException(fieldRequiredMessage(outcome, fieldName));
        }
    }

    private static String fieldRequiredMessage(ParentFollowUpOutcome outcome, String fieldName) {
        return fieldName + " is required for parentFollowUpOutcome " + outcome;
    }
}
