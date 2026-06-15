package com.genixo.education.search.service.appointment;

import com.genixo.education.search.dto.appointment.BulkAppointmentOperationDto;
import com.genixo.education.search.entity.appointment.Appointment;
import com.genixo.education.search.enumaration.AppointmentOutcome;
import com.genixo.education.search.enumaration.AppointmentStatus;
import com.genixo.education.search.enumaration.ParentFollowUpOutcome;
import org.springframework.util.StringUtils;

public final class ParentFollowUpOutcomeApplier {

    private ParentFollowUpOutcomeApplier() {
    }

    public static void apply(Appointment appointment, BulkAppointmentOperationDto bulkDto, Long userId) {
        ParentFollowUpOutcome parentFollowUpOutcome = bulkDto.getParentFollowUpOutcome();
        if (parentFollowUpOutcome == null) {
            return;
        }

        AppointmentStatus status = bulkDto.getNewStatus() != null
                ? bulkDto.getNewStatus()
                : ParentFollowUpOutcomeValidator.resolveDefaultStatus(parentFollowUpOutcome);

        appointment.setStatus(status);
        appointment.setParentFollowUpOutcome(parentFollowUpOutcome);
        appointment.setFollowUpRequired(
                ParentFollowUpOutcomeValidator.resolveFollowUpRequired(parentFollowUpOutcome, bulkDto.getFollowUpRequired()));
        appointment.setOutcomeNotes(StringUtils.hasText(bulkDto.getOutcomeNotes()) ? bulkDto.getOutcomeNotes().trim() : null);
        appointment.setUpdatedBy(userId);

        if (requiresFollowUpSchedule(parentFollowUpOutcome)) {
            appointment.setFollowUpDate(bulkDto.getFollowUpDate());
            appointment.setFollowUpTime(bulkDto.getFollowUpTime());
        } else {
            appointment.setFollowUpDate(null);
            appointment.setFollowUpTime(null);
        }

        if (requiresNoSaleReason(parentFollowUpOutcome)) {
            appointment.setNoSaleReason(bulkDto.getNoSaleReason().trim());
        } else {
            appointment.setNoSaleReason(null);
        }

        if (parentFollowUpOutcome == ParentFollowUpOutcome.ENROLLED) {
            appointment.setOutcome(AppointmentOutcome.ENROLLED);
        }

        applyStatusSideEffects(appointment, status, userId);
        appendFollowUpSummary(appointment, bulkDto);
    }

    private static void applyStatusSideEffects(Appointment appointment, AppointmentStatus status, Long userId) {
        switch (status) {
            case COMPLETED -> {
                appointment.setActualStartTime(
                        java.time.LocalDateTime.of(appointment.getAppointmentDate(), appointment.getStartTime()));
                appointment.setActualEndTime(java.time.LocalDateTime.now());
            }
            case NO_SHOW -> appointment.setActualEndTime(java.time.LocalDateTime.now());
            case CONFIRMED -> {
                if (appointment.getConfirmedAt() == null) {
                    appointment.setConfirmedAt(java.time.LocalDateTime.now());
                    appointment.setConfirmedBy(userId);
                }
            }
            default -> {
                // no-op
            }
        }
    }

    private static boolean requiresFollowUpSchedule(ParentFollowUpOutcome outcome) {
        return outcome == ParentFollowUpOutcome.TO_FOLLOW_UP
                || outcome == ParentFollowUpOutcome.APPOINTMENT_POSTPONED
                || outcome == ParentFollowUpOutcome.WAITING_FOR_INFO
                || outcome == ParentFollowUpOutcome.DID_NOT_ATTEND;
    }

    private static boolean requiresNoSaleReason(ParentFollowUpOutcome outcome) {
        return outcome == ParentFollowUpOutcome.TO_FOLLOW_UP
                || outcome == ParentFollowUpOutcome.WONT_FOLLOW_UP;
    }

    private static void appendFollowUpSummary(Appointment appointment, BulkAppointmentOperationDto bulkDto) {
        String summary = StringUtils.hasText(bulkDto.getCustomMessage())
                ? bulkDto.getCustomMessage().trim()
                : ParentFollowUpOutcomeValidator.buildSummaryMessage(bulkDto);

        if (!StringUtils.hasText(summary)) {
            return;
        }

        if (StringUtils.hasText(appointment.getInternalNotes())) {
            appointment.setInternalNotes(appointment.getInternalNotes() + "\n\n" + summary);
        } else {
            appointment.setInternalNotes(summary);
        }
    }
}
