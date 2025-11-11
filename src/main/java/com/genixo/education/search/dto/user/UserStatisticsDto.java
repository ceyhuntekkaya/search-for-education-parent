package com.genixo.education.search.dto.user;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserStatisticsDto {
    private Long totalUsers;
    private Long activeUsers;
    private Long inactiveUsers;
    private Long institutionUsers;
    private Long parentUsers;
    private Long usersRegisteredToday;
    private Long usersRegisteredThisWeek;
    private Long usersRegisteredThisMonth;
    private Long usersLoggedInToday;
    private Long usersLoggedInThisWeek;
    private Long unverifiedEmailUsers;
    private Long unverifiedPhoneUsers;
}
