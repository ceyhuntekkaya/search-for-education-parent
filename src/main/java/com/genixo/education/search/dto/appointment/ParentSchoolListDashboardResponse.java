package com.genixo.education.search.dto.appointment;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ParentSchoolListDashboardResponse {

    private Integer totalLists;
    private Integer totalSchools;
    private Integer favoriteSchools;
    private Integer blockedSchools;
    private Integer schoolsWithAppointments;
    private Integer pendingVisits;
    private Integer completedVisits;
    private Integer upcomingReminderCount;

    private List<ParentSchoolListSummaryResponse> recentLists;
    private List<ParentSchoolListItemResponse> recentlyAddedSchools;
    private List<ParentSchoolNoteResponse> upcomingReminderNotes;
}