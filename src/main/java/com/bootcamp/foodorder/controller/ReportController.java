package com.bootcamp.foodorder.controller;

import com.bootcamp.foodorder.dto.SalesReportResponse;
import com.bootcamp.foodorder.service.ReportService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/reports")
@Tag(name = "Report", description = "Sales report endpoints for admin")
@SecurityRequirement(name = "bearerAuth")
public class ReportController {

    private final ReportService reportService;

    public ReportController(ReportService reportService) {
        this.reportService = reportService;
    }

    @GetMapping("/sales")
    public ResponseEntity<SalesReportResponse> getSalesReport(
            @RequestParam(required = false) String period,
            @RequestParam(required = false) String date
    ) {
        SalesReportResponse response = reportService.getSalesReport(period, date);
        return ResponseEntity.ok(response);
    }
}
