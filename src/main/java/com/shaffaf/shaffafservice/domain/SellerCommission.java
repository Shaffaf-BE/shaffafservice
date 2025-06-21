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
 * A SellerCommission.
 */
@Entity
@Table(name = "seller_commission")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@SuppressWarnings("common-java:DuplicatedBlocks")
public class SellerCommission implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator", sequenceName = "seller_commission_seq")
    @Column(name = "id")
    private Long id;

    @Column(name = "commission_month")
    private Integer commissionMonth;

    @Column(name = "commission_year")
    private Integer commissionYear;

    @Column(name = "commission_amount", precision = 21, scale = 2)
    private BigDecimal commissionAmount;

    @Column(name = "commission_paid_on")
    private Instant commissionPaidOn;

    @Column(name = "commission_paid_by")
    private String commissionPaidBy;

    @NotNull
    @Column(name = "phone_number", nullable = false)
    private String phoneNumber;

    @Column(name = "created_by")
    private String createdBy;

    @Column(name = "created_date")
    private Instant createdDate;

    @Column(name = "last_modified_by")
    private String lastModifiedBy;

    @Column(name = "last_modified_date")
    private Instant lastModifiedDate;

    @Column(name = "deleted_on")
    private Instant deletedOn;

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

    public SellerCommission id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getCommissionMonth() {
        return this.commissionMonth;
    }

    public SellerCommission commissionMonth(Integer commissionMonth) {
        this.setCommissionMonth(commissionMonth);
        return this;
    }

    public void setCommissionMonth(Integer commissionMonth) {
        this.commissionMonth = commissionMonth;
    }

    public Integer getCommissionYear() {
        return this.commissionYear;
    }

    public SellerCommission commissionYear(Integer commissionYear) {
        this.setCommissionYear(commissionYear);
        return this;
    }

    public void setCommissionYear(Integer commissionYear) {
        this.commissionYear = commissionYear;
    }

    public BigDecimal getCommissionAmount() {
        return this.commissionAmount;
    }

    public SellerCommission commissionAmount(BigDecimal commissionAmount) {
        this.setCommissionAmount(commissionAmount);
        return this;
    }

    public void setCommissionAmount(BigDecimal commissionAmount) {
        this.commissionAmount = commissionAmount;
    }

    public Instant getCommissionPaidOn() {
        return this.commissionPaidOn;
    }

    public SellerCommission commissionPaidOn(Instant commissionPaidOn) {
        this.setCommissionPaidOn(commissionPaidOn);
        return this;
    }

    public void setCommissionPaidOn(Instant commissionPaidOn) {
        this.commissionPaidOn = commissionPaidOn;
    }

    public String getCommissionPaidBy() {
        return this.commissionPaidBy;
    }

    public SellerCommission commissionPaidBy(String commissionPaidBy) {
        this.setCommissionPaidBy(commissionPaidBy);
        return this;
    }

    public void setCommissionPaidBy(String commissionPaidBy) {
        this.commissionPaidBy = commissionPaidBy;
    }

    public String getPhoneNumber() {
        return this.phoneNumber;
    }

    public SellerCommission phoneNumber(String phoneNumber) {
        this.setPhoneNumber(phoneNumber);
        return this;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getCreatedBy() {
        return this.createdBy;
    }

    public SellerCommission createdBy(String createdBy) {
        this.setCreatedBy(createdBy);
        return this;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public Instant getCreatedDate() {
        return this.createdDate;
    }

    public SellerCommission createdDate(Instant createdDate) {
        this.setCreatedDate(createdDate);
        return this;
    }

    public void setCreatedDate(Instant createdDate) {
        this.createdDate = createdDate;
    }

    public String getLastModifiedBy() {
        return this.lastModifiedBy;
    }

    public SellerCommission lastModifiedBy(String lastModifiedBy) {
        this.setLastModifiedBy(lastModifiedBy);
        return this;
    }

    public void setLastModifiedBy(String lastModifiedBy) {
        this.lastModifiedBy = lastModifiedBy;
    }

    public Instant getLastModifiedDate() {
        return this.lastModifiedDate;
    }

    public SellerCommission lastModifiedDate(Instant lastModifiedDate) {
        this.setLastModifiedDate(lastModifiedDate);
        return this;
    }

    public void setLastModifiedDate(Instant lastModifiedDate) {
        this.lastModifiedDate = lastModifiedDate;
    }

    public Instant getDeletedOn() {
        return this.deletedOn;
    }

    public SellerCommission deletedOn(Instant deletedOn) {
        this.setDeletedOn(deletedOn);
        return this;
    }

    public void setDeletedOn(Instant deletedOn) {
        this.deletedOn = deletedOn;
    }

    public Project getProject() {
        return this.project;
    }

    public void setProject(Project project) {
        this.project = project;
    }

    public SellerCommission project(Project project) {
        this.setProject(project);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof SellerCommission)) {
            return false;
        }
        return getId() != null && getId().equals(((SellerCommission) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "SellerCommission{" +
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
            "}";
    }
}
