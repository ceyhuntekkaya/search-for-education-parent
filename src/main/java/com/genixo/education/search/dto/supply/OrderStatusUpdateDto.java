package com.genixo.education.search.dto.supply;

import com.genixo.education.search.enumaration.OrderStatus;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderStatusUpdateDto {

    @NotNull(message = "Status is required")
    private OrderStatus status;
}

