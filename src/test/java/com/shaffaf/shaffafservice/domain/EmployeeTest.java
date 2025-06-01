package com.shaffaf.shaffafservice.domain;

import static com.shaffaf.shaffafservice.domain.EmployeeTestSamples.*;
import static com.shaffaf.shaffafservice.domain.ProjectTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.shaffaf.shaffafservice.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class EmployeeTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Employee.class);
        Employee employee1 = getEmployeeSample1();
        Employee employee2 = new Employee();
        assertThat(employee1).isNotEqualTo(employee2);

        employee2.setId(employee1.getId());
        assertThat(employee1).isEqualTo(employee2);

        employee2 = getEmployeeSample2();
        assertThat(employee1).isNotEqualTo(employee2);
    }

    @Test
    void projectTest() {
        Employee employee = getEmployeeRandomSampleGenerator();
        Project projectBack = getProjectRandomSampleGenerator();

        employee.setProject(projectBack);
        assertThat(employee.getProject()).isEqualTo(projectBack);

        employee.project(null);
        assertThat(employee.getProject()).isNull();
    }
}
