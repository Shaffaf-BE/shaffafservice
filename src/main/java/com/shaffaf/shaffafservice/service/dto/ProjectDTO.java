package com.shaffaf.shaffafservice.service.dto;

import com.shaffaf.shaffafservice.domain.enumeration.Status;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.Objects;

/**
 * A DTO for the {@link com.shaffaf.shaffafservice.domain.Project} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class ProjectDTO implements Serializable {

    private Long id;

    @NotNull
    private String name;

    private String description;

    @NotNull
    private LocalDate startDate;

    private LocalDate endDate;

    @NotNull
    private Status status;

    private BigDecimal feesPerUnitPerMonth;

    @NotNull
    private String unionHeadName;

    @NotNull
    private String unionHeadMobileNumber;

    @NotNull
    private Integer numberOfUnits;

    private String consentProvidedBy;

    private Instant consentProvidedOn;

    private String createdBy;

    private Instant createdDate;

    private String lastModifiedBy;

    private Instant lastModifiedDate;

    private Instant deletedDate;

    private SellerDTO seller;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public BigDecimal getFeesPerUnitPerMonth() {
        return feesPerUnitPerMonth;
    }

    public void setFeesPerUnitPerMonth(BigDecimal feesPerUnitPerMonth) {
        this.feesPerUnitPerMonth = feesPerUnitPerMonth;
    }

    public String getUnionHeadName() {
        return unionHeadName;
    }

    public void setUnionHeadName(String unionHeadName) {
        this.unionHeadName = unionHeadName;
    }

    public String getUnionHeadMobileNumber() {
        return unionHeadMobileNumber;
    }

    public void setUnionHeadMobileNumber(String unionHeadMobileNumber) {
        this.unionHeadMobileNumber = unionHeadMobileNumber;
    }

    public Integer getNumberOfUnits() {
        return numberOfUnits;
    }

    public void setNumberOfUnits(Integer numberOfUnits) {
        this.numberOfUnits = numberOfUnits;
    }

    public String getConsentProvidedBy() {
        return consentProvidedBy;
    }

    public void setConsentProvidedBy(String consentProvidedBy) {
        this.consentProvidedBy = consentProvidedBy;
    }

    public Instant getConsentProvidedOn() {
        return consentProvidedOn;
    }

    public void setConsentProvidedOn(Instant consentProvidedOn) {
        this.consentProvidedOn = consentProvidedOn;
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

    public Instant getDeletedDate() {
        return deletedDate;
    }

    public void setDeletedDate(Instant deletedDate) {
        this.deletedDate = deletedDate;
    }

    public SellerDTO getSeller() {
        return seller;
    }

    public void setSeller(SellerDTO seller) {
        this.seller = seller;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ProjectDTO)) {
            return false;
        }

        ProjectDTO projectDTO = (ProjectDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, projectDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "ProjectDTO{" +
            "id=" + getId() +
            ", name='" + getName() + "'" +
            ", description='" + getDescription() + "'" +
            ", startDate='" + getStartDate() + "'" +
            ", endDate='" + getEndDate() + "'" +
            ", status='" + getStatus() + "'" +
            ", feesPerUnitPerMonth=" + getFeesPerUnitPerMonth() +
            ", unionHeadName='" + getUnionHeadName() + "'" +
            ", unionHeadMobileNumber='" + getUnionHeadMobileNumber() + "'" +
            ", numberOfUnits=" + getNumberOfUnits() +
            ", consentProvidedBy='" + getConsentProvidedBy() + "'" +
            ", consentProvidedOn='" + getConsentProvidedOn() + "'" +
            ", createdBy='" + getCreatedBy() + "'" +
            ", createdDate='" + getCreatedDate() + "'" +
            ", lastModifiedBy='" + getLastModifiedBy() + "'" +
            ", lastModifiedDate='" + getLastModifiedDate() + "'" +
            ", deletedDate='" + getDeletedDate() + "'" +
            ", seller=" + getSeller() +
            "}";
    }
}
