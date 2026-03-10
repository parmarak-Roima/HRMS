package com.HRMS.HRMS.OrgChart;

import com.HRMS.HRMS.entity.Employee;
import com.HRMS.HRMS.entity.Enums.Designations;
import com.HRMS.HRMS.entity.Role;
import com.HRMS.HRMS.repository.EmployeeRepository;
import com.HRMS.HRMS.repository.RoleRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class OrgChartControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private RoleRepository roleRepository;

    @BeforeEach
    public void setupDatabase() {
        employeeRepository.deleteAll();
        Employee emp = new Employee();
        emp.setName("Test Employee");
        emp.setEmail("test@roima.com");
        emp.setManager(null);
        emp.setDesignation(Designations.JR_SOFTWARE_DEVELOPER);
        emp.setProfileUrl("hello");
        emp.setPasswordHash("Alasdair");
        emp.setBirthdate(LocalDate.now());
        emp.setJoiningDate(LocalDate.now());
//        emp.setRole(role);
        employeeRepository.save(emp);
    }

    @Test
    public void testGetChartByEmail_Integration() throws Exception {
        String accessToken  = "eyJhbGciOiJIUzI1NiJ9.eyJyb2xlIjoiRU1QTE9ZRUUiLCJuYW1lIjoiRTEyIiwiaWQiOjM2LCJzdWIiOiJycDYzNDU5MjRAZ21haWwuY29tIiwiaWF0IjoxNzczMDM2ODczLCJleHAiOjE3NzMwNzI4NzN9.Nu27bu-R95Uk5pdZETeQfEsmcW-xTn8pfFiYgeSA9hE";
        mockMvc.perform(get("/org-chart/email/{email}", "test@roima.com" ).header("Authorization", "Bearer " + accessToken)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Org chart fetched"))
                .andExpect(jsonPath("$.data.selectedEmployee.email").value("test@roima.com"));
    }
}