package com.genixo.education.search.service.converter;

import com.genixo.education.search.dto.appointment.*;
import com.genixo.education.search.entity.appointment.ParentListNote;
import com.genixo.education.search.entity.appointment.ParentSchoolList;
import com.genixo.education.search.entity.appointment.ParentSchoolListItem;
import com.genixo.education.search.entity.appointment.ParentSchoolNote;
import com.genixo.education.search.enumaration.SchoolItemStatus;
import com.genixo.education.search.repository.appointment.ParentListNoteRepository;
import com.genixo.education.search.repository.appointment.ParentSchoolListItemRepository;
import com.genixo.education.search.repository.appointment.ParentSchoolNoteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ParentListConverterService {

    private final ParentSchoolListItemRepository parentSchoolListItemRepository;
    private final ParentSchoolNoteRepository parentSchoolNoteRepository;
    private final ParentListNoteRepository parentListNoteRepository;

    // ========================= PARENT SCHOOL LIST =========================

    /**
     * ParentSchoolList entity'sini ParentSchoolListResponse DTO'suna dönüştürür
     */
    public ParentSchoolListResponse mapToDto(ParentSchoolList entity) {
        if (entity == null) {
            return null;
        }

        // İstatistikleri hesapla
        Integer favoriteCount = parentSchoolListItemRepository
                .findByParentSchoolListIdAndStatusOrderByPriorityOrderAscCreatedAtDesc(
                        entity.getId(),
                        SchoolItemStatus.ACTIVE)
                .stream()
                .mapToInt(item -> item.getIsFavorite() != null && item.getIsFavorite() ? 1 : 0)
                .sum();

        Integer blockedCount = parentSchoolListItemRepository
                .findByParentSchoolListIdAndStatusOrderByPriorityOrderAscCreatedAtDesc(
                        entity.getId(),
                        SchoolItemStatus.ACTIVE)
                .stream()
                .mapToInt(item -> item.getIsBlocked() != null && item.getIsBlocked() ? 1 : 0)
                .sum();

        Integer visitPlannedCount = parentSchoolListItemRepository
                .findByParentSchoolListIdAndStatusOrderByPriorityOrderAscCreatedAtDesc(
                        entity.getId(),
                        SchoolItemStatus.ACTIVE)
                .stream()
                .mapToInt(item -> item.getVisitPlannedDate() != null && item.getVisitCompletedDate() == null ? 1 : 0)
                .sum();

        Integer visitCompletedCount = parentSchoolListItemRepository
                .findByParentSchoolListIdAndStatusOrderByPriorityOrderAscCreatedAtDesc(
                        entity.getId(),
                        SchoolItemStatus.ACTIVE)
                .stream()
                .mapToInt(item -> item.getVisitCompletedDate() != null ? 1 : 0)
                .sum();

        return ParentSchoolListResponse.builder()
                .id(entity.getId())
                .listName(entity.getListName())
                .description(entity.getDescription())
                .status(entity.getStatus())
                .isDefault(entity.getIsDefault())
                .colorCode(entity.getColorCode())
                .icon(entity.getIcon())
                .notes(entity.getNotes())
                .schoolCount(entity.getSchoolCount())
                .lastAccessedAt(entity.getLastAccessedAt())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .favoriteCount(favoriteCount)
                .blockedCount(blockedCount)
                .visitPlannedCount(visitPlannedCount)
                .visitCompletedCount(visitCompletedCount)
                .build();
    }

    /**
     * ParentSchoolList listesini ParentSchoolListResponse listesine dönüştürür
     */
    public List<ParentSchoolListResponse> mapToDto(List<ParentSchoolList> entities) {
        if (entities == null) {
            return null;
        }
        return entities.stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    /**
     * ParentSchoolList entity'sini ParentSchoolListSummaryResponse DTO'suna dönüştürür
     */
    public ParentSchoolListSummaryResponse mapToSummaryDto(ParentSchoolList entity) {
        if (entity == null) {
            return null;
        }

        return ParentSchoolListSummaryResponse.builder()
                .id(entity.getId())
                .listName(entity.getListName())
                .colorCode(entity.getColorCode())
                .icon(entity.getIcon())
                .schoolCount(entity.getSchoolCount())
                .isDefault(entity.getIsDefault())
                .lastAccessedAt(entity.getLastAccessedAt())
                .build();
    }

    /**
     * ParentSchoolList listesini ParentSchoolListSummaryResponse listesine dönüştürür
     */
    public List<ParentSchoolListSummaryResponse> mapToSummaryDto(List<ParentSchoolList> entities) {
        if (entities == null) {
            return null;
        }
        return entities.stream()
                .map(this::mapToSummaryDto)
                .collect(Collectors.toList());
    }

    // ========================= PARENT SCHOOL LIST ITEM =========================

    /**
     * ParentSchoolListItem entity'sini ParentSchoolListItemResponse DTO'suna dönüştürür
     */
    public ParentSchoolListItemResponse mapToDto(ParentSchoolListItem entity) {
        if (entity == null) {
            return null;
        }

        // Not sayılarını hesapla
        Integer noteCount = parentSchoolNoteRepository.countByParentSchoolListItemId(entity.getId());
        Integer importantNoteCount = parentSchoolNoteRepository.countImportantByParentSchoolListItemId(entity.getId());

        return ParentSchoolListItemResponse.builder()
                .id(entity.getId())
                .schoolId(entity.getSchool().getId())
                .schoolName(entity.getSchool().getName())
                .schoolSlug(entity.getSchool().getSlug())
                .schoolLogoUrl(entity.getSchool().getLogoUrl())
                .schoolPhone(entity.getSchool().getPhone())
                .schoolEmail(entity.getSchool().getEmail())
                .schoolRatingAverage(entity.getSchool().getRatingAverage())
                .schoolRatingCount(entity.getSchool().getRatingCount())
                .starRating(entity.getStarRating())
                .isFavorite(entity.getIsFavorite())
                .isBlocked(entity.getIsBlocked())
                .priorityOrder(entity.getPriorityOrder())
                .personalNotes(entity.getPersonalNotes())
                .pros(entity.getPros())
                .cons(entity.getCons())
                .tags(entity.getTags())
                .visitPlannedDate(entity.getVisitPlannedDate())
                .visitCompletedDate(entity.getVisitCompletedDate())
                .addedFromSearch(entity.getAddedFromSearch())
                .appointmentCount(0) // Bu randevu repository'den gelecek
                .lastAppointmentDate(null) // Bu randevu repository'den gelecek
                .nextAppointmentDate(null) // Bu randevu repository'den gelecek
                .noteCount(noteCount)
                .importantNoteCount(importantNoteCount)
                .addedAt(entity.getCreatedAt())
                .lastUpdatedAt(entity.getLastUpdatedAt())
                .build();
    }

    /**
     * ParentSchoolListItem listesini ParentSchoolListItemResponse listesine dönüştürür
     */
    public List<ParentSchoolListItemResponse> mapToListItemDto(List<ParentSchoolListItem> entities) {
        if (entities == null) {
            return null;
        }
        return entities.stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    // ========================= PARENT SCHOOL NOTE =========================

    /**
     * ParentSchoolNote entity'sini ParentSchoolNoteResponse DTO'suna dönüştürür
     */
    public ParentSchoolNoteResponse mapToDto(ParentSchoolNote entity) {
        if (entity == null) {
            return null;
        }

        return ParentSchoolNoteResponse.builder()
                .id(entity.getId())
                .schoolId(entity.getSchool().getId())
                .schoolName(entity.getSchool().getName())
                .parentSchoolListItemId(entity.getParentSchoolListItem() != null ?
                        entity.getParentSchoolListItem().getId() : null)
                .noteTitle(entity.getNoteTitle())
                .noteContent(entity.getNoteContent())
                .category(entity.getCategory() != null ? entity.getCategory().toString() : null)
                .isImportant(entity.getIsImportant())
                .reminderDate(entity.getReminderDate())
                .source(entity.getSource())
                .attachmentUrl(entity.getAttachmentUrl())
                .attachmentName(entity.getAttachmentName())
                .attachmentSize(entity.getAttachmentSize())
                .attachmentType(entity.getAttachmentType())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }

    /**
     * ParentSchoolNote listesini ParentSchoolNoteResponse listesine dönüştürür
     */
    public List<ParentSchoolNoteResponse> mapToSchoolNoteDto(List<ParentSchoolNote> entities) {
        if (entities == null) {
            return null;
        }
        return entities.stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    // ========================= PARENT LIST NOTE =========================

    /**
     * ParentListNote entity'sini ParentListNoteResponse DTO'suna dönüştürür
     */
    public ParentListNoteResponse mapToDto(ParentListNote entity) {
        if (entity == null) {
            return null;
        }

        return ParentListNoteResponse.builder()
                .id(entity.getId())
                .parentSchoolListId(entity.getParentSchoolList().getId())
                .listName(entity.getParentSchoolList().getListName())
                .noteTitle(entity.getNoteTitle())
                .noteContent(entity.getNoteContent())
                .isImportant(entity.getIsImportant())
                .reminderDate(entity.getReminderDate())
                .attachmentUrl(entity.getAttachmentUrl())
                .attachmentName(entity.getAttachmentName())
                .attachmentSize(entity.getAttachmentSize())
                .attachmentType(entity.getAttachmentType())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }

    /**
     * ParentListNote listesini ParentListNoteResponse listesine dönüştürür
     */
    public List<ParentListNoteResponse> mapToListNoteDto(List<ParentListNote> entities) {
        if (entities == null) {
            return null;
        }
        return entities.stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    // ========================= DASHBOARD =========================

    /**
     * Dashboard için özet bilgileri hazırlar
     */
    public ParentSchoolListDashboardResponse mapToDashboardDto(
            Long parentUserId,
            Integer totalLists,
            Integer totalSchools,
            Integer favoriteSchools,
            Integer blockedSchools,
            Integer schoolsWithAppointments,
            Integer pendingVisits,
            Integer completedVisits,
            Integer upcomingReminderCount,
            List<ParentSchoolList> recentLists,
            List<ParentSchoolListItem> recentlyAddedSchools,
            List<ParentSchoolNote> upcomingReminderNotes) {

        return ParentSchoolListDashboardResponse.builder()
                .totalLists(totalLists)
                .totalSchools(totalSchools)
                .favoriteSchools(favoriteSchools)
                .blockedSchools(blockedSchools)
                .schoolsWithAppointments(schoolsWithAppointments)
                .pendingVisits(pendingVisits)
                .completedVisits(completedVisits)
                .upcomingReminderCount(upcomingReminderCount)
                .recentLists(mapToSummaryDto(recentLists))
                .recentlyAddedSchools(mapToListItemDto(recentlyAddedSchools))
                .upcomingReminderNotes(mapToSchoolNoteDto(upcomingReminderNotes))
                .build();
    }
}