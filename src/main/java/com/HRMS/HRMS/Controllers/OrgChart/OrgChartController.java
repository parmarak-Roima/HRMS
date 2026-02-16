package com.HRMS.HRMS.Controllers.OrgChart;

import com.HRMS.HRMS.dto.ApiResponse;
import com.HRMS.HRMS.dto.EmailDtos.EmailSendingDto;
import com.HRMS.HRMS.dto.OrgChart.OrgChartDto;
import com.HRMS.HRMS.service.Email.EmailService;
import com.HRMS.HRMS.service.OrgChart.OrgChartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/org-chart")
public class OrgChartController {

    private final OrgChartService orgChartService;
    private final EmailService emailService;

    @Autowired
    public OrgChartController(OrgChartService orgChartService, EmailService emailService){
        this.orgChartService = orgChartService;
        this.emailService = emailService;
    }

    @GetMapping("/id/{employeeId}")
    public ResponseEntity<ApiResponse<OrgChartDto>> getChart(@PathVariable Long employeeId) {
        OrgChartDto chart = orgChartService.getOrgChart(employeeId);
        return ResponseEntity.ok(new ApiResponse<>("Org chart fetched", chart));
    }
    @GetMapping("/email/{email}")
    public ResponseEntity<ApiResponse<OrgChartDto>> getChart(@PathVariable String email) {
        OrgChartDto chart = orgChartService.getOrgChartByName(email);
        return ResponseEntity.ok(new ApiResponse<>("Org chart fetched", chart));
    }
}
