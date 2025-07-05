package com.shaffaf.shaffafservice.service.dto;

import java.io.Serializable;
import java.time.Instant;

/**
 * DTO for bulk unit information response.
 */
public class BulkUnitInfoDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long unitId;
    private String unitNumber;
    private Long blockId;
    private String blockName;
    private Long unitTypeId;
    private String unitTypeName;
    private Long projectId;
    private String projectName;
    private String createdBy;
    private Instant createdDate;

    public BulkUnitInfoDTO() {}

    public BulkUnitInfoDTO(
        Long unitId,
        String unitNumber,
        Long blockId,
        String blockName,
        Long unitTypeId,
        String unitTypeName,
        Long projectId,
        String projectName,
        String createdBy,
        Instant createdDate
    ) {
        this.unitId = unitId;
        this.unitNumber = unitNumber;
        this.blockId = blockId;
        this.blockName = blockName;
        this.unitTypeId = unitTypeId;
        this.unitTypeName = unitTypeName;
        this.projectId = projectId;
        this.projectName = projectName;
        this.createdBy = createdBy;
        this.createdDate = createdDate;
    }

    // Getters and Setters
    public Long getUnitId() {
        return unitId;
    }

    public void setUnitId(Long unitId) {
        this.unitId = unitId;
    }

    public String getUnitNumber() {
        return unitNumber;
    }

    public void setUnitNumber(String unitNumber) {
        this.unitNumber = unitNumber;
    }

    public Long getBlockId() {
        return blockId;
    }

    public void setBlockId(Long blockId) {
        this.blockId = blockId;
    }

    public String getBlockName() {
        return blockName;
    }

    public void setBlockName(String blockName) {
        this.blockName = blockName;
    }

    public Long getUnitTypeId() {
        return unitTypeId;
    }

    public void setUnitTypeId(Long unitTypeId) {
        this.unitTypeId = unitTypeId;
    }

    public String getUnitTypeName() {
        return unitTypeName;
    }

    public void setUnitTypeName(String unitTypeName) {
        this.unitTypeName = unitTypeName;
    }

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

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public Instant getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Instant createdDate) {
        this.createdDate = createdDate;
    }

    @Override
    public String toString() {
        return (
            "BulkUnitInfoDTO{" +
            "unitId=" +
            unitId +
            ", unitNumber='" +
            unitNumber +
            '\'' +
            ", blockId=" +
            blockId +
            ", blockName='" +
            blockName +
            '\'' +
            ", unitTypeId=" +
            unitTypeId +
            ", unitTypeName='" +
            unitTypeName +
            '\'' +
            ", projectId=" +
            projectId +
            ", projectName='" +
            projectName +
            '\'' +
            ", createdBy='" +
            createdBy +
            '\'' +
            ", createdDate=" +
            createdDate +
            '}'
        );
    }
}
