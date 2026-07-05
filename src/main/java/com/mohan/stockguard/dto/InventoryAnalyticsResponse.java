package com.mohan.stockguard.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InventoryAnalyticsResponse {
    private Long totalProducts;
    private Long lowStockCount;
    private Long outOfStockCount;
    private List<TopProductDto> topProducts;
    private List<LowStockAlertDto> lowStockAlerts;

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class TopProductDto {
        private Long productId;
        private String productName;
        private Integer totalOrdered;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class LowStockAlertDto {
        private Long productId;
        private String productName;
        private Integer currentStock;
        private Integer threshold;
    }
}
