package com.genixo.education.search.dto.subscription;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UsageUpdateDto {

private Integer schoolCountDelta;
private Integer userCountDelta;
private Integer appointmentCountDelta;
private Integer galleryItemCountDelta;
private Integer postCountDelta;
private Long storageDeltaMb;

}
