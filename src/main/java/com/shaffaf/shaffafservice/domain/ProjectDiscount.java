package com.shaffaf.shaffafservice.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.Instant;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * A ProjectDiscount.
 */
@Entity
@Table(name = "project_discount")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@SuppressWarnings("common-java:DuplicatedBlocks")
public class ProjectDiscount implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator", sequenceName = "project_discount_seq")
    @Column(name = "id")
    private Long id;

    @NotNull
    @Column(name = "title", nullable = false)
    private String title;

    @NotNull
    @Column(name = "discount_start_date", nullable = false)
    private Instant discountStartDate;

    @NotNull
    @Column(name = "discount_end_date", nullable = false)
    private Instant discountEndDate;

    @NotNull
    @Column(name = "discount", precision = 21, scale = 2, nullable = false)
    private BigDecimal discount;

    @Column(name = "created_by")
    private String createdBy;

    @Column(name = "created_date")
    private Instant createdDate;

    @Column(name = "last_modified_by")
    private String lastModifiedBy;

    @Column(name = "last_modified_date")
    private Instant lastModifiedDate;

    @Column(name = "deleted_date")
    private Instant deletedDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnoreProperties(
        value = {
            "sellerCommissions",
            "projectDiscounts",
            "unionMembers",
            "collectors",
            "blocks",
            "expenseTypes",
            "notices",
            "complainTypes",
            "seller",
        },
        allowSetters = true
    )
    private Project project;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public ProjectDiscount id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return this.title;
    }

    public ProjectDiscount title(String title) {
        this.setTitle(title);
        return this;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Instant getDiscountStartDate() {
        return this.discountStartDate;
    }

    public ProjectDiscount discountStartDate(Instant discountStartDate) {
        this.setDiscountStartDate(discountStartDate);
        return this;
    }

    public void setDiscountStartDate(Instant discountStartDate) {
        this.discountStartDate = discountStartDate;
    }

    public Instant getDiscountEndDate() {
        return this.discountEndDate;
    }

    public ProjectDiscount discountEndDate(Instant discountEndDate) {
        this.setDiscountEndDate(discountEndDate);
        return this;
    }

    public void setDiscountEndDate(Instant discountEndDate) {
        this.discountEndDate = discountEndDate;
    }

    public BigDecimal getDiscount() {
        return this.discount;
    }

    public ProjectDiscount discount(BigDecimal discount) {
        this.setDiscount(discount);
        return this;
    }

    public void setDiscount(BigDecimal discount) {
        this.discount = discount;
    }

    public String getCreatedBy() {
        return this.createdBy;
    }

    public ProjectDiscount createdBy(String createdBy) {
        this.setCreatedBy(createdBy);
        return this;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public Instant getCreatedDate() {
        return this.createdDate;
    }

    public ProjectDiscount createdDate(Instant createdDate) {
        this.setCreatedDate(createdDate);
        return this;
    }

    public void setCreatedDate(Instant createdDate) {
        this.createdDate = createdDate;
    }

    public String getLastModifiedBy() {
        return this.lastModifiedBy;
    }

    public ProjectDiscount lastModifiedBy(String lastModifiedBy) {
        this.setLastModifiedBy(lastModifiedBy);
        return this;
    }

    public void setLastModifiedBy(String lastModifiedBy) {
        this.lastModifiedBy = lastModifiedBy;
    }

    public Instant getLastModifiedDate() {
        return this.lastModifiedDate;
    }

    public ProjectDiscount lastModifiedDate(Instant lastModifiedDate) {
        this.setLastModifiedDate(lastModifiedDate);
        return this;
    }

    public void setLastModifiedDate(Instant lastModifiedDate) {
        this.lastModifiedDate = lastModifiedDate;
    }

    public Instant getDeletedDate() {
        return this.deletedDate;
    }

    public ProjectDiscount deletedDate(Instant deletedDate) {
        this.setDeletedDate(deletedDate);
        return this;
    }

    public void setDeletedDate(Instant deletedDate) {
        this.deletedDate = deletedDate;
    }

    public Project getProject() {
        return this.project;
    }

    public void setProject(Project project) {
        this.project = project;
    }

    public ProjectDiscount project(Project project) {
        this.setProject(project);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ProjectDiscount)) {
            return false;
        }
        return getId() != null && getId().equals(((ProjectDiscount) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "ProjectDiscount{" +
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
            "}";
    }
}
