package com.HRMS.HRMS.OrgChart;

import com.HRMS.HRMS.dto.OrgChart.OrgChartDto;
import com.HRMS.HRMS.entity.Employee;
import com.HRMS.HRMS.entity.Enums.Designations;
import com.HRMS.HRMS.exception.ResourceNotFoundException;
import com.HRMS.HRMS.repository.EmployeeRepository;
import com.HRMS.HRMS.service.OrgChart.OrgChartService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrgChartServiceTest {
    @Mock
    private EmployeeRepository employeeRepository;

    @Spy
    private ModelMapper modelMapper = new ModelMapper();

    @InjectMocks
    private OrgChartService orgChartService;

    private Employee ceo;
    private Employee manager;
    private Employee employee;

    @BeforeEach
    void setUp() {
        // Build a fake hierarchy before each test runs
        ceo = new Employee();
        ceo.setName("CEO");
        ceo.setEmail("ceo@roima.com");
        ceo.setSubordinates(new ArrayList<>());
        ceo.setDesignation(Designations.CEO);
        ceo.setProfileUrl("hello");
        ceo.setPasswordHash("Alasdair");
        ceo.setBirthdate(LocalDate.now());
        ceo.setJoiningDate(LocalDate.now());

        manager = new Employee();
        manager.setName("Manager");
        manager.setEmail("manager@roima.com");
        manager.setManager(ceo);
        manager.setSubordinates(new ArrayList<>());
        ceo.getSubordinates().add(manager);
        manager.setDesignation(Designations.SR_SOFTWARE_DEVELOPER);
        manager.setProfileUrl("hello");
        manager.setPasswordHash("Alasdair");
        manager.setBirthdate(LocalDate.now());
        manager.setJoiningDate(LocalDate.now());

        employee = new Employee();
        employee.setName("Employee");
        employee.setEmail("employee@roima.com");
        employee.setManager(manager);
        employee.setSubordinates(new ArrayList<>());
        manager.getSubordinates().add(employee);
        employee.setDesignation(Designations.JR_SOFTWARE_DEVELOPER);
        employee.setProfileUrl("hello");
        employee.setPasswordHash("Alasdair");
        employee.setBirthdate(LocalDate.now());
        employee.setJoiningDate(LocalDate.now());
    }

    @Test
    void getOrgChart_WhenEmployeeExists() {
        // set operations
        when(employeeRepository.findById(3L)).thenReturn(Optional.of(employee));
        // call the method
        OrgChartDto result = orgChartService.getOrgChart(3L);
        // result must be not null
        assertNotNull(result);
        //there must be no direct reports for employee
        assertEquals("Employee", result.getSelectedEmployee().getName());
        // direct reports will be 0 for employee
        assertTrue(result.getDirectReports().isEmpty());
        // path from root --> upper level hierarchy
        assertEquals(2, result.getPathFromRoot().size());
        assertEquals("CEO", result.getPathFromRoot().get(0).getName());
        assertEquals("Manager", result.getPathFromRoot().get(1).getName());
    }
    @Test
    void getOrgChart_WhenEmployeeIsCeo() {
        when(employeeRepository.findById(1L)).thenReturn(Optional.of(ceo));
        OrgChartDto result = orgChartService.getOrgChart(1L);
        assertEquals("CEO", result.getSelectedEmployee().getName());
        assertEquals(1, result.getDirectReports().size());
        assertEquals("Manager", result.getDirectReports().get(0).getName());
        assertTrue(result.getPathFromRoot().isEmpty());
    }
    @Test
    void getOrgChart_WhenEmployeeNotFound() {
        when(employeeRepository.findById(99L)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> {
            orgChartService.getOrgChart(99L);
        });
    }
    @Test
    void getOrgChartByName_WhenEmailExists_ReturnsCompleteChart() {
        when(employeeRepository.findByEmail("manager@roima.com")).thenReturn(Optional.of(manager));
        OrgChartDto result = orgChartService.getOrgChartByName("manager@roima.com");
        assertNotNull(result);
        assertEquals("Manager", result.getSelectedEmployee().getName());

        assertEquals(1, result.getDirectReports().size());
        assertEquals("Employee", result.getDirectReports().get(0).getName());

        assertEquals(1, result.getPathFromRoot().size());
        assertEquals("CEO", result.getPathFromRoot().get(0).getName());
    }
    @Test
    void getOrgChartByName_WhenEmailNotFound() {
        when(employeeRepository.findByEmail("u@roima.com")).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> {
            orgChartService.getOrgChartByName("u@roima.com");
        });
    }

}
