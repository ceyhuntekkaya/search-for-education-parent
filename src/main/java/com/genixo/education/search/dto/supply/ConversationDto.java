package com.genixo.education.search.dto.supply;

import com.genixo.education.search.enumaration.ProductMessageType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ConversationDto {

    private Long id;
    private Long companyId;
    private String companyName;
    private Long supplierId;
    private String supplierCompanyName;
    private Long productId;
    private String productName;
    private Long quotationId;
    private Long orderId;
    private String orderNumber;
    private ProductMessageType messageType;
    private String subject;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Long unreadCount;
    private Long messageCount;
    private LocalDateTime lastMessageAt;
}

