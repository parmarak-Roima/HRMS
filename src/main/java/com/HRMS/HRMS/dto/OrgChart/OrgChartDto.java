package com.HRMS.HRMS.dto.OrgChart;

import lombok.Data;
import java.util.List;

@Data
public class OrgChartDto {
    private List<EmployeeSummaryDto> pathFromRoot;
    private EmployeeSummaryDto selectedEmployee;
    private List<EmployeeSummaryDto> directReports;
}
