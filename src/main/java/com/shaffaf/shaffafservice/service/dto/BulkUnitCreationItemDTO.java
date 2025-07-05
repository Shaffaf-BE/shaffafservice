package com.shaffaf.shaffafservice.service.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * DTO for bulk unit creation request item.
 */
public class BulkUnitCreationItemDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    @NotBlank(message = "Block name cannot be blank")
    private String block;

    @NotBlank(message = "Unit type name cannot be blank")
    private String unitType;

    @NotNull(message = "Unit start number is required")
    @Min(value = 1, message = "Unit start number must be at least 1")
    private Integer unitStart;

    @NotNull(message = "Unit end number is required")
    @Min(value = 1, message = "Unit end number must be at least 1")
    private Integer unitEnd;

    public BulkUnitCreationItemDTO() {}

    public BulkUnitCreationItemDTO(String block, String unitType, Integer unitStart, Integer unitEnd) {
        this.block = block;
        this.unitType = unitType;
        this.unitStart = unitStart;
        this.unitEnd = unitEnd;
    }

    public String getBlock() {
        return block;
    }

    public void setBlock(String block) {
        this.block = block;
    }

    public String getUnitType() {
        return unitType;
    }

    public void setUnitType(String unitType) {
        this.unitType = unitType;
    }

    public Integer getUnitStart() {
        return unitStart;
    }

    public void setUnitStart(Integer unitStart) {
        this.unitStart = unitStart;
    }

    public Integer getUnitEnd() {
        return unitEnd;
    }

    public void setUnitEnd(Integer unitEnd) {
        this.unitEnd = unitEnd;
    }

    @Override
    public String toString() {
        return (
            "BulkUnitCreationItemDTO{" +
            "block='" +
            block +
            '\'' +
            ", unitType='" +
            unitType +
            '\'' +
            ", unitStart=" +
            unitStart +
            ", unitEnd=" +
            unitEnd +
            '}'
        );
    }
}
