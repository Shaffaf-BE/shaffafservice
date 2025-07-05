package com.shaffaf.shaffafservice.service.dto;

import java.io.Serializable;
import java.util.List;

/**
 * DTO for bulk unit creation response.
 */
public class BulkUnitCreationResponseDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    private String message;
    private Integer totalUnitsCreated;
    private Integer totalBlocksCreated;
    private Integer totalUnitTypesCreated;
    private List<String> createdBlocks;
    private List<String> createdUnitTypes;
    private List<String> warnings;

    public BulkUnitCreationResponseDTO() {}

    public BulkUnitCreationResponseDTO(
        String message,
        Integer totalUnitsCreated,
        Integer totalBlocksCreated,
        Integer totalUnitTypesCreated,
        List<String> createdBlocks,
        List<String> createdUnitTypes,
        List<String> warnings
    ) {
        this.message = message;
        this.totalUnitsCreated = totalUnitsCreated;
        this.totalBlocksCreated = totalBlocksCreated;
        this.totalUnitTypesCreated = totalUnitTypesCreated;
        this.createdBlocks = createdBlocks;
        this.createdUnitTypes = createdUnitTypes;
        this.warnings = warnings;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Integer getTotalUnitsCreated() {
        return totalUnitsCreated;
    }

    public void setTotalUnitsCreated(Integer totalUnitsCreated) {
        this.totalUnitsCreated = totalUnitsCreated;
    }

    public Integer getTotalBlocksCreated() {
        return totalBlocksCreated;
    }

    public void setTotalBlocksCreated(Integer totalBlocksCreated) {
        this.totalBlocksCreated = totalBlocksCreated;
    }

    public Integer getTotalUnitTypesCreated() {
        return totalUnitTypesCreated;
    }

    public void setTotalUnitTypesCreated(Integer totalUnitTypesCreated) {
        this.totalUnitTypesCreated = totalUnitTypesCreated;
    }

    public List<String> getCreatedBlocks() {
        return createdBlocks;
    }

    public void setCreatedBlocks(List<String> createdBlocks) {
        this.createdBlocks = createdBlocks;
    }

    public List<String> getCreatedUnitTypes() {
        return createdUnitTypes;
    }

    public void setCreatedUnitTypes(List<String> createdUnitTypes) {
        this.createdUnitTypes = createdUnitTypes;
    }

    public List<String> getWarnings() {
        return warnings;
    }

    public void setWarnings(List<String> warnings) {
        this.warnings = warnings;
    }

    @Override
    public String toString() {
        return (
            "BulkUnitCreationResponseDTO{" +
            "message='" +
            message +
            '\'' +
            ", totalUnitsCreated=" +
            totalUnitsCreated +
            ", totalBlocksCreated=" +
            totalBlocksCreated +
            ", totalUnitTypesCreated=" +
            totalUnitTypesCreated +
            ", createdBlocks=" +
            createdBlocks +
            ", createdUnitTypes=" +
            createdUnitTypes +
            ", warnings=" +
            warnings +
            '}'
        );
    }
}
