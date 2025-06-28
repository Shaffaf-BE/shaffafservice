package com.shaffaf.shaffafservice.service.dto;

import java.math.BigDecimal;
import java.time.Instant;

/**
 * DTO for individual project data in seller's personal dashboard.
 */
public class SellerProjectDTO {

    private Long projectId;
    private String projectName;
    private BigDecimal amount;
    private Integer numberOfUnits;
    private BigDecimal feesPerUnit;
    private Instant createdDate;
    private String status;
    private String description;
    private BigDecimal totalRevenue;
    private Integer daysActive;

    public SellerProjectDTO() {}

    public SellerProjectDTO(
        Long projectId,
        String projectName,
        BigDecimal amount,
        Integer numberOfUnits,
        BigDecimal feesPerUnit,
        Instant createdDate,
        String status,
        String description,
        BigDecimal totalRevenue,
        Integer daysActive
    ) {
        this.projectId = projectId;
        this.projectName = projectName;
        this.amount = amount;
        this.numberOfUnits = numberOfUnits;
        this.feesPerUnit = feesPerUnit;
        this.createdDate = createdDate;
        this.status = status;
        this.description = description;
        this.totalRevenue = totalRevenue;
        this.daysActive = daysActive;
    }

    // Getters and Setters
    public Long getProjectId() {
        return projectId;
    }

    public void setProjectId(Long projectId) {
        this.projectId = projectId;
    }

    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public Integer getNumberOfUnits() {
        return numberOfUnits;
    }

    public void setNumberOfUnits(Integer numberOfUnits) {
        this.numberOfUnits = numberOfUnits;
    }

    public BigDecimal getFeesPerUnit() {
        return feesPerUnit;
    }

    public void setFeesPerUnit(BigDecimal feesPerUnit) {
        this.feesPerUnit = feesPerUnit;
    }

    public Instant getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Instant createdDate) {
        this.createdDate = createdDate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public BigDecimal getTotalRevenue() {
        return totalRevenue;
    }

    public void setTotalRevenue(BigDecimal totalRevenue) {
        this.totalRevenue = totalRevenue;
    }

    public Integer getDaysActive() {
        return daysActive;
    }

    public void setDaysActive(Integer daysActive) {
        this.daysActive = daysActive;
    }

    @Override
    public String toString() {
        return (
            "SellerProjectDTO{" +
            "projectId=" +
            projectId +
            ", projectName='" +
            projectName +
            '\'' +
            ", amount=" +
            amount +
            ", numberOfUnits=" +
            numberOfUnits +
            ", feesPerUnit=" +
            feesPerUnit +
            ", createdDate=" +
            createdDate +
            ", status='" +
            status +
            '\'' +
            ", description='" +
            description +
            '\'' +
            ", totalRevenue=" +
            totalRevenue +
            ", daysActive=" +
            daysActive +
            '}'
        );
    }
}
