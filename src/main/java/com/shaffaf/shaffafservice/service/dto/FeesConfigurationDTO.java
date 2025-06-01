package com.shaffaf.shaffafservice.service.dto;

import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.Objects;

/**
 * A DTO for the {@link com.shaffaf.shaffafservice.domain.FeesConfiguration} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class FeesConfigurationDTO implements Serializable {

    private Long id;

    @NotNull
    private String title;

    private String description;

    @NotNull
    private BigDecimal amount;

    @NotNull
    private Boolean isRecurring;

    private LocalDate dueDate;

    private String configuredBy;

    private String createdBy;

    private Instant createdDate;

    private String lastModifiedBy;

    private Instant lastModifiedDate;

    private Instant deletedOn;

    private UnitTypeDTO unitType;

    private BlockDTO block;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public Boolean getIsRecurring() {
        return isRecurring;
    }

    public void setIsRecurring(Boolean isRecurring) {
        this.isRecurring = isRecurring;
    }

    public LocalDate getDueDate() {
        return dueDate;
    }

    public void setDueDate(LocalDate dueDate) {
        this.dueDate = dueDate;
    }

    public String getConfiguredBy() {
        return configuredBy;
    }

    public void setConfiguredBy(String configuredBy) {
        this.configuredBy = configuredBy;
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

    public String getLastModifiedBy() {
        return lastModifiedBy;
    }

    public void setLastModifiedBy(String lastModifiedBy) {
        this.lastModifiedBy = lastModifiedBy;
    }

    public Instant getLastModifiedDate() {
        return lastModifiedDate;
    }

    public void setLastModifiedDate(Instant lastModifiedDate) {
        this.lastModifiedDate = lastModifiedDate;
    }

    public Instant getDeletedOn() {
        return deletedOn;
    }

    public void setDeletedOn(Instant deletedOn) {
        this.deletedOn = deletedOn;
    }

    public UnitTypeDTO getUnitType() {
        return unitType;
    }

    public void setUnitType(UnitTypeDTO unitType) {
        this.unitType = unitType;
    }

    public BlockDTO getBlock() {
        return block;
    }

    public void setBlock(BlockDTO block) {
        this.block = block;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof FeesConfigurationDTO)) {
            return false;
        }

        FeesConfigurationDTO feesConfigurationDTO = (FeesConfigurationDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, feesConfigurationDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "FeesConfigurationDTO{" +
            "id=" + getId() +
            ", title='" + getTitle() + "'" +
            ", description='" + getDescription() + "'" +
            ", amount=" + getAmount() +
            ", isRecurring='" + getIsRecurring() + "'" +
            ", dueDate='" + getDueDate() + "'" +
            ", configuredBy='" + getConfiguredBy() + "'" +
            ", createdBy='" + getCreatedBy() + "'" +
            ", createdDate='" + getCreatedDate() + "'" +
            ", lastModifiedBy='" + getLastModifiedBy() + "'" +
            ", lastModifiedDate='" + getLastModifiedDate() + "'" +
            ", deletedOn='" + getDeletedOn() + "'" +
            ", unitType=" + getUnitType() +
            ", block=" + getBlock() +
            "}";
    }
}
