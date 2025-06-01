package com.shaffaf.shaffafservice.service.dto;

import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.Objects;

/**
 * A DTO for the {@link com.shaffaf.shaffafservice.domain.SellerCommission} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class SellerCommissionDTO implements Serializable {

    private Long id;

    private Integer commissionMonth;

    private Integer commissionYear;

    private BigDecimal commissionAmount;

    private Instant commissionPaidOn;

    private String commissionPaidBy;

    @NotNull
    private String phoneNumber;

    private String createdBy;

    private Instant createdDate;

    private String lastModifiedBy;

    private Instant lastModifiedDate;

    private Instant deletedOn;

    private ProjectDTO project;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getCommissionMonth() {
        return commissionMonth;
    }

    public void setCommissionMonth(Integer commissionMonth) {
        this.commissionMonth = commissionMonth;
    }

    public Integer getCommissionYear() {
        return commissionYear;
    }

    public void setCommissionYear(Integer commissionYear) {
        this.commissionYear = commissionYear;
    }

    public BigDecimal getCommissionAmount() {
        return commissionAmount;
    }

    public void setCommissionAmount(BigDecimal commissionAmount) {
        this.commissionAmount = commissionAmount;
    }

    public Instant getCommissionPaidOn() {
        return commissionPaidOn;
    }

    public void setCommissionPaidOn(Instant commissionPaidOn) {
        this.commissionPaidOn = commissionPaidOn;
    }

    public String getCommissionPaidBy() {
        return commissionPaidBy;
    }

    public void setCommissionPaidBy(String commissionPaidBy) {
        this.commissionPaidBy = commissionPaidBy;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
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

    public ProjectDTO getProject() {
        return project;
    }

    public void setProject(ProjectDTO project) {
        this.project = project;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof SellerCommissionDTO)) {
            return false;
        }

        SellerCommissionDTO sellerCommissionDTO = (SellerCommissionDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, sellerCommissionDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "SellerCommissionDTO{" +
            "id=" + getId() +
            ", commissionMonth=" + getCommissionMonth() +
            ", commissionYear=" + getCommissionYear() +
            ", commissionAmount=" + getCommissionAmount() +
            ", commissionPaidOn='" + getCommissionPaidOn() + "'" +
            ", commissionPaidBy='" + getCommissionPaidBy() + "'" +
            ", phoneNumber='" + getPhoneNumber() + "'" +
            ", createdBy='" + getCreatedBy() + "'" +
            ", createdDate='" + getCreatedDate() + "'" +
            ", lastModifiedBy='" + getLastModifiedBy() + "'" +
            ", lastModifiedDate='" + getLastModifiedDate() + "'" +
            ", deletedOn='" + getDeletedOn() + "'" +
            ", project=" + getProject() +
            "}";
    }
}
