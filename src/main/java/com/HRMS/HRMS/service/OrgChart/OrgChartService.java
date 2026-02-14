package com.HRMS.HRMS.service.OrgChart;

import com.HRMS.HRMS.dto.OrgChart.EmployeeSummaryDto;
import com.HRMS.HRMS.dto.OrgChart.OrgChartDto;
import com.HRMS.HRMS.entity.Employee;
import com.HRMS.HRMS.exception.ResourceNotFoundException;
import com.HRMS.HRMS.repository.EmployeeRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class OrgChartService {

    private final EmployeeRepository employeeRepository;
    private final ModelMapper modelMapper;

    @Autowired
    public OrgChartService( EmployeeRepository employeeRepository ,ModelMapper modelMapper ){
        this.employeeRepository = employeeRepository;
        this.modelMapper = modelMapper;
    }

    public OrgChartDto getOrgChart(Long employeeId) {
        Employee focus = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found"));

        OrgChartDto response = new OrgChartDto();
        //set employee whose chart will be shown
        response.setSelectedEmployee(modelMapper.map(focus,EmployeeSummaryDto.class));

        //direct reports
        List<EmployeeSummaryDto> reports = focus.getSubordinates().stream()
                .map(
                        employee -> {
                            return modelMapper.map(employee, EmployeeSummaryDto.class);
                        }
                )
                .toList();
        response.setDirectReports(reports);

        //ancestors
        List<EmployeeSummaryDto> ancestors = new ArrayList<>();
        Employee current = focus.getManager();

        while (current != null) {
            ancestors.add(0, modelMapper.map(current, EmployeeSummaryDto.class));
            current = current.getManager();
        }
        response.setPathFromRoot(ancestors);

        return response;
    }

    public OrgChartDto getOrgChartByName(String email) {
        Employee focus = employeeRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found"));

        OrgChartDto response = new OrgChartDto();
        //set employee whose chart will be shown
        response.setSelectedEmployee(modelMapper.map(focus,EmployeeSummaryDto.class));

        //direct reports
        List<EmployeeSummaryDto> reports = focus.getSubordinates().stream()
                .map(
                        employee -> {
                            return modelMapper.map(employee, EmployeeSummaryDto.class);
                        }
                )
                .toList();
        response.setDirectReports(reports);

        //ancestors
        List<EmployeeSummaryDto> ancestors = new ArrayList<>();
        Employee current = focus.getManager();

        while (current != null) {
            ancestors.add(0, modelMapper.map(current, EmployeeSummaryDto.class));
            current = current.getManager();
        }
        response.setPathFromRoot(ancestors);

        return response;
    }
}
