package com.genixo.education.search.dto.subscription;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UsageLimitsDto {
    private Integer schoolsUsed;
    private Integer schoolsLimit;
    private Integer usersUsed;
    private Integer usersLimit;
    private Integer appointmentsThisMonth;
    private Integer appointmentsLimit;
    private Integer galleryItemsUsed;
    private Integer galleryItemsLimit;
    private Integer postsThisMonth;
    private Integer postsLimit;
    private Long storageUsedMb;
    private Integer storageLimit;



}
