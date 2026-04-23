package com.bootcamp.foodorder.service;

import com.bootcamp.foodorder.dto.SalesReportResponse;
import com.bootcamp.foodorder.dto.TopSellingMenuResponse;
import com.bootcamp.foodorder.entity.Order;
import com.bootcamp.foodorder.entity.OrderItem;
import com.bootcamp.foodorder.entity.OrderStatus;
import com.bootcamp.foodorder.repository.OrderItemRepository;
import com.bootcamp.foodorder.repository.OrderRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ReportService {

    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;

    public ReportService(OrderRepository orderRepository, OrderItemRepository orderItemRepository) {
        this.orderRepository = orderRepository;
        this.orderItemRepository = orderItemRepository;
    }

    public SalesReportResponse getSalesReport(String period, String date) {
        List<Order> orders;
        String reportPeriod;
        String reportDate;

        if (period == null || period.isBlank()) {
            orders = orderRepository.findByStatus(OrderStatus.PAID);
            reportPeriod = "all";
            reportDate = "-";
        } else if ("daily".equalsIgnoreCase(period)) {
            if (date == null || date.isBlank()) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Date is required for daily report. Format: yyyy-MM-dd");
            }

            LocalDate localDate;
            try {
                localDate = LocalDate.parse(date);
            } catch (Exception e) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid daily date format. Use yyyy-MM-dd");
            }

            LocalDateTime start = localDate.atStartOfDay();
            LocalDateTime end = localDate.plusDays(1).atStartOfDay();

            orders = orderRepository.findByStatusAndCreatedAtBetween(OrderStatus.PAID, start, end);
            reportPeriod = "daily";
            reportDate = date;
        } else if ("monthly".equalsIgnoreCase(period)) {
            if (date == null || date.isBlank()) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Date is required for monthly report. Format: yyyy-MM");
            }

            YearMonth yearMonth;
            try {
                yearMonth = YearMonth.parse(date);
            } catch (Exception e) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid monthly date format. Use yyyy-MM");
            }

            LocalDateTime start = yearMonth.atDay(1).atStartOfDay();
            LocalDateTime end = yearMonth.plusMonths(1).atDay(1).atStartOfDay();

            orders = orderRepository.findByStatusAndCreatedAtBetween(OrderStatus.PAID, start, end);
            reportPeriod = "monthly";
            reportDate = date;
        } else {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Period must be one of: daily, monthly");
        }

        BigDecimal totalSales = orders.stream()
                .map(Order::getTotalAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        long totalOrders = orders.size();

        List<TopSellingMenuResponse> topSellingMenus = buildTopSellingMenus(orders);

        return SalesReportResponse.builder()
                .period(reportPeriod)
                .date(reportDate)
                .totalSales(totalSales)
                .totalOrders(totalOrders)
                .topSellingMenus(topSellingMenus)
                .build();
    }

    private List<TopSellingMenuResponse> buildTopSellingMenus(List<Order> orders) {
        if (orders.isEmpty()) {
            return Collections.emptyList();
        }

        List<OrderItem> orderItems = orderItemRepository.findByOrderIn(orders);

        Map<Long, TopMenuAccumulator> menuMap = new HashMap<>();

        for (OrderItem orderItem : orderItems) {
            Long menuId = orderItem.getMenu().getId();

            TopMenuAccumulator existing = menuMap.get(menuId);

            if (existing == null) {
                menuMap.put(menuId, new TopMenuAccumulator(
                        menuId,
                        orderItem.getMenu().getName(),
                        orderItem.getQuantity()
                ));
            } else {
                existing.totalSold += orderItem.getQuantity();
            }
        }

        return menuMap.values().stream()
                .sorted((a, b) -> Integer.compare(b.totalSold, a.totalSold))
                .limit(5)
                .map(item -> TopSellingMenuResponse.builder()
                        .menuId(item.menuId)
                        .menuName(item.menuName)
                        .totalSold(item.totalSold)
                        .build())
                .collect(Collectors.toList());
    }

    private static class TopMenuAccumulator {
        private Long menuId;
        private String menuName;
        private Integer totalSold;

        public TopMenuAccumulator(Long menuId, String menuName, Integer totalSold) {
            this.menuId = menuId;
            this.menuName = menuName;
            this.totalSold = totalSold;
        }
    }
}
