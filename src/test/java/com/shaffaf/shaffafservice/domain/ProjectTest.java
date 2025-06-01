package com.shaffaf.shaffafservice.domain;

import static com.shaffaf.shaffafservice.domain.BlockTestSamples.*;
import static com.shaffaf.shaffafservice.domain.ComplainTypeTestSamples.*;
import static com.shaffaf.shaffafservice.domain.EmployeeTestSamples.*;
import static com.shaffaf.shaffafservice.domain.ExpenseTypeTestSamples.*;
import static com.shaffaf.shaffafservice.domain.NoticeTestSamples.*;
import static com.shaffaf.shaffafservice.domain.ProjectDiscountTestSamples.*;
import static com.shaffaf.shaffafservice.domain.ProjectTestSamples.*;
import static com.shaffaf.shaffafservice.domain.SellerCommissionTestSamples.*;
import static com.shaffaf.shaffafservice.domain.SellerTestSamples.*;
import static com.shaffaf.shaffafservice.domain.UnionMemberTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.shaffaf.shaffafservice.web.rest.TestUtil;
import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.Test;

class ProjectTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Project.class);
        Project project1 = getProjectSample1();
        Project project2 = new Project();
        assertThat(project1).isNotEqualTo(project2);

        project2.setId(project1.getId());
        assertThat(project1).isEqualTo(project2);

        project2 = getProjectSample2();
        assertThat(project1).isNotEqualTo(project2);
    }

    @Test
    void sellerCommissionsTest() {
        Project project = getProjectRandomSampleGenerator();
        SellerCommission sellerCommissionBack = getSellerCommissionRandomSampleGenerator();

        project.addSellerCommissions(sellerCommissionBack);
        assertThat(project.getSellerCommissions()).containsOnly(sellerCommissionBack);
        assertThat(sellerCommissionBack.getProject()).isEqualTo(project);

        project.removeSellerCommissions(sellerCommissionBack);
        assertThat(project.getSellerCommissions()).doesNotContain(sellerCommissionBack);
        assertThat(sellerCommissionBack.getProject()).isNull();

        project.sellerCommissions(new HashSet<>(Set.of(sellerCommissionBack)));
        assertThat(project.getSellerCommissions()).containsOnly(sellerCommissionBack);
        assertThat(sellerCommissionBack.getProject()).isEqualTo(project);

        project.setSellerCommissions(new HashSet<>());
        assertThat(project.getSellerCommissions()).doesNotContain(sellerCommissionBack);
        assertThat(sellerCommissionBack.getProject()).isNull();
    }

    @Test
    void projectDiscountTest() {
        Project project = getProjectRandomSampleGenerator();
        ProjectDiscount projectDiscountBack = getProjectDiscountRandomSampleGenerator();

        project.addProjectDiscount(projectDiscountBack);
        assertThat(project.getProjectDiscounts()).containsOnly(projectDiscountBack);
        assertThat(projectDiscountBack.getProject()).isEqualTo(project);

        project.removeProjectDiscount(projectDiscountBack);
        assertThat(project.getProjectDiscounts()).doesNotContain(projectDiscountBack);
        assertThat(projectDiscountBack.getProject()).isNull();

        project.projectDiscounts(new HashSet<>(Set.of(projectDiscountBack)));
        assertThat(project.getProjectDiscounts()).containsOnly(projectDiscountBack);
        assertThat(projectDiscountBack.getProject()).isEqualTo(project);

        project.setProjectDiscounts(new HashSet<>());
        assertThat(project.getProjectDiscounts()).doesNotContain(projectDiscountBack);
        assertThat(projectDiscountBack.getProject()).isNull();
    }

    @Test
    void unionMembersTest() {
        Project project = getProjectRandomSampleGenerator();
        UnionMember unionMemberBack = getUnionMemberRandomSampleGenerator();

        project.addUnionMembers(unionMemberBack);
        assertThat(project.getUnionMembers()).containsOnly(unionMemberBack);
        assertThat(unionMemberBack.getProject()).isEqualTo(project);

        project.removeUnionMembers(unionMemberBack);
        assertThat(project.getUnionMembers()).doesNotContain(unionMemberBack);
        assertThat(unionMemberBack.getProject()).isNull();

        project.unionMembers(new HashSet<>(Set.of(unionMemberBack)));
        assertThat(project.getUnionMembers()).containsOnly(unionMemberBack);
        assertThat(unionMemberBack.getProject()).isEqualTo(project);

        project.setUnionMembers(new HashSet<>());
        assertThat(project.getUnionMembers()).doesNotContain(unionMemberBack);
        assertThat(unionMemberBack.getProject()).isNull();
    }

    @Test
    void collectorsTest() {
        Project project = getProjectRandomSampleGenerator();
        Employee employeeBack = getEmployeeRandomSampleGenerator();

        project.addCollectors(employeeBack);
        assertThat(project.getCollectors()).containsOnly(employeeBack);
        assertThat(employeeBack.getProject()).isEqualTo(project);

        project.removeCollectors(employeeBack);
        assertThat(project.getCollectors()).doesNotContain(employeeBack);
        assertThat(employeeBack.getProject()).isNull();

        project.collectors(new HashSet<>(Set.of(employeeBack)));
        assertThat(project.getCollectors()).containsOnly(employeeBack);
        assertThat(employeeBack.getProject()).isEqualTo(project);

        project.setCollectors(new HashSet<>());
        assertThat(project.getCollectors()).doesNotContain(employeeBack);
        assertThat(employeeBack.getProject()).isNull();
    }

    @Test
    void blocksTest() {
        Project project = getProjectRandomSampleGenerator();
        Block blockBack = getBlockRandomSampleGenerator();

        project.addBlocks(blockBack);
        assertThat(project.getBlocks()).containsOnly(blockBack);
        assertThat(blockBack.getProject()).isEqualTo(project);

        project.removeBlocks(blockBack);
        assertThat(project.getBlocks()).doesNotContain(blockBack);
        assertThat(blockBack.getProject()).isNull();

        project.blocks(new HashSet<>(Set.of(blockBack)));
        assertThat(project.getBlocks()).containsOnly(blockBack);
        assertThat(blockBack.getProject()).isEqualTo(project);

        project.setBlocks(new HashSet<>());
        assertThat(project.getBlocks()).doesNotContain(blockBack);
        assertThat(blockBack.getProject()).isNull();
    }

    @Test
    void expenseTypesTest() {
        Project project = getProjectRandomSampleGenerator();
        ExpenseType expenseTypeBack = getExpenseTypeRandomSampleGenerator();

        project.addExpenseTypes(expenseTypeBack);
        assertThat(project.getExpenseTypes()).containsOnly(expenseTypeBack);
        assertThat(expenseTypeBack.getProject()).isEqualTo(project);

        project.removeExpenseTypes(expenseTypeBack);
        assertThat(project.getExpenseTypes()).doesNotContain(expenseTypeBack);
        assertThat(expenseTypeBack.getProject()).isNull();

        project.expenseTypes(new HashSet<>(Set.of(expenseTypeBack)));
        assertThat(project.getExpenseTypes()).containsOnly(expenseTypeBack);
        assertThat(expenseTypeBack.getProject()).isEqualTo(project);

        project.setExpenseTypes(new HashSet<>());
        assertThat(project.getExpenseTypes()).doesNotContain(expenseTypeBack);
        assertThat(expenseTypeBack.getProject()).isNull();
    }

    @Test
    void noticesTest() {
        Project project = getProjectRandomSampleGenerator();
        Notice noticeBack = getNoticeRandomSampleGenerator();

        project.addNotices(noticeBack);
        assertThat(project.getNotices()).containsOnly(noticeBack);
        assertThat(noticeBack.getProject()).isEqualTo(project);

        project.removeNotices(noticeBack);
        assertThat(project.getNotices()).doesNotContain(noticeBack);
        assertThat(noticeBack.getProject()).isNull();

        project.notices(new HashSet<>(Set.of(noticeBack)));
        assertThat(project.getNotices()).containsOnly(noticeBack);
        assertThat(noticeBack.getProject()).isEqualTo(project);

        project.setNotices(new HashSet<>());
        assertThat(project.getNotices()).doesNotContain(noticeBack);
        assertThat(noticeBack.getProject()).isNull();
    }

    @Test
    void complainTypesTest() {
        Project project = getProjectRandomSampleGenerator();
        ComplainType complainTypeBack = getComplainTypeRandomSampleGenerator();

        project.addComplainTypes(complainTypeBack);
        assertThat(project.getComplainTypes()).containsOnly(complainTypeBack);
        assertThat(complainTypeBack.getProject()).isEqualTo(project);

        project.removeComplainTypes(complainTypeBack);
        assertThat(project.getComplainTypes()).doesNotContain(complainTypeBack);
        assertThat(complainTypeBack.getProject()).isNull();

        project.complainTypes(new HashSet<>(Set.of(complainTypeBack)));
        assertThat(project.getComplainTypes()).containsOnly(complainTypeBack);
        assertThat(complainTypeBack.getProject()).isEqualTo(project);

        project.setComplainTypes(new HashSet<>());
        assertThat(project.getComplainTypes()).doesNotContain(complainTypeBack);
        assertThat(complainTypeBack.getProject()).isNull();
    }

    @Test
    void sellerTest() {
        Project project = getProjectRandomSampleGenerator();
        Seller sellerBack = getSellerRandomSampleGenerator();

        project.setSeller(sellerBack);
        assertThat(project.getSeller()).isEqualTo(sellerBack);

        project.seller(null);
        assertThat(project.getSeller()).isNull();
    }
}
