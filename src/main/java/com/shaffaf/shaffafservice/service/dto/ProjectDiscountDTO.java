package com.shaffaf.shaffafservice.service.dto;

import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.Objects;

/**
 * A DTO for the {@link com.shaffaf.shaffafservice.domain.ProjectDiscount} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class ProjectDiscountDTO implements Serializable {

    private Long id;

    @NotNull
    private String title;

    @NotNull
    private Instant discountStartDate;

    @NotNull
    private Instant discountEndDate;

    @NotNull
    private BigDecimal discount;

    private String createdBy;

    private Instant createdDate;

    private String lastModifiedBy;

    private Instant lastModifiedDate;

    private Instant deletedDate;

    private ProjectDTO project;

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

    public Instant getDiscountStartDate() {
        return discountStartDate;
    }

    public void setDiscountStartDate(Instant discountStartDate) {
        this.discountStartDate = discountStartDate;
    }

    public Instant getDiscountEndDate() {
        return discountEndDate;
    }

    public void setDiscountEndDate(Instant discountEndDate) {
        this.discountEndDate = discountEndDate;
    }

    public BigDecimal getDiscount() {
        return discount;
    }

    public void setDiscount(BigDecimal discount) {
        this.discount = discount;
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
        if (!(o instanceof ProjectDiscountDTO)) {
            return false;
        }

        ProjectDiscountDTO projectDiscountDTO = (ProjectDiscountDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, projectDiscountDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "ProjectDiscountDTO{" +
            "id=" + getId() +
            ", title='" + getTitle() + "'" +
            ", discountStartDate='" + getDiscountStartDate() + "'" +
            ", discountEndDate='" + getDiscountEndDate() + "'" +
            ", discount=" + getDiscount() +
            ", createdBy='" + getCreatedBy() + "'" +
            ", createdDate='" + getCreatedDate() + "'" +
            ", lastModifiedBy='" + getLastModifiedBy() + "'" +
            ", lastModifiedDate='" + getLastModifiedDate() + "'" +
            ", deletedDate='" + getDeletedDate() + "'" +
            ", project=" + getProject() +
            "}";
    }
}
