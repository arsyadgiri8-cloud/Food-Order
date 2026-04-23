package com.bootcamp.foodorder.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@Builder
public class SalesReportResponse {

    private String period;
    private String date;
    private BigDecimal totalSales;
    private Long totalOrders;
    private List<TopSellingMenuResponse> topSellingMenus;
}
