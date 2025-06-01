package com.shaffaf.shaffafservice.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.shaffaf.shaffafservice.domain.enumeration.Status;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * A Project.
 */
@Entity
@Table(name = "project")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@SuppressWarnings("common-java:DuplicatedBlocks")
public class Project implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @NotNull
    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "description")
    private String description;

    @NotNull
    @Column(name = "start_date", nullable = false)
    private LocalDate startDate;

    @Column(name = "end_date")
    private LocalDate endDate;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private Status status;

    @Column(name = "fees_per_unit_per_month", precision = 21, scale = 2)
    private BigDecimal feesPerUnitPerMonth;

    @NotNull
    @Column(name = "union_head_name", nullable = false)
    private String unionHeadName;

    @NotNull
    @Column(name = "union_head_mobile_number", nullable = false)
    private String unionHeadMobileNumber;

    @NotNull
    @Column(name = "number_of_units", nullable = false)
    private Integer numberOfUnits;

    @Column(name = "consent_provided_by")
    private String consentProvidedBy;

    @Column(name = "consent_provided_on")
    private Instant consentProvidedOn;

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

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "project")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @JsonIgnoreProperties(value = { "project" }, allowSetters = true)
    private Set<SellerCommission> sellerCommissions = new HashSet<>();

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "project")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @JsonIgnoreProperties(value = { "project" }, allowSetters = true)
    private Set<ProjectDiscount> projectDiscounts = new HashSet<>();

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "project")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @JsonIgnoreProperties(value = { "project" }, allowSetters = true)
    private Set<UnionMember> unionMembers = new HashSet<>();

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "project")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @JsonIgnoreProperties(value = { "project" }, allowSetters = true)
    private Set<Employee> collectors = new HashSet<>();

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "project")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @JsonIgnoreProperties(value = { "units", "feesConfigurations", "project" }, allowSetters = true)
    private Set<Block> blocks = new HashSet<>();

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "project")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @JsonIgnoreProperties(value = { "expenses", "project" }, allowSetters = true)
    private Set<ExpenseType> expenseTypes = new HashSet<>();

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "project")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @JsonIgnoreProperties(value = { "project" }, allowSetters = true)
    private Set<Notice> notices = new HashSet<>();

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "project")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @JsonIgnoreProperties(value = { "complains", "project" }, allowSetters = true)
    private Set<ComplainType> complainTypes = new HashSet<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnoreProperties(value = { "projects" }, allowSetters = true)
    private Seller seller;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Project id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return this.name;
    }

    public Project name(String name) {
        this.setName(name);
        return this;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return this.description;
    }

    public Project description(String description) {
        this.setDescription(description);
        return this;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDate getStartDate() {
        return this.startDate;
    }

    public Project startDate(LocalDate startDate) {
        this.setStartDate(startDate);
        return this;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getEndDate() {
        return this.endDate;
    }

    public Project endDate(LocalDate endDate) {
        this.setEndDate(endDate);
        return this;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    public Status getStatus() {
        return this.status;
    }

    public Project status(Status status) {
        this.setStatus(status);
        return this;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public BigDecimal getFeesPerUnitPerMonth() {
        return this.feesPerUnitPerMonth;
    }

    public Project feesPerUnitPerMonth(BigDecimal feesPerUnitPerMonth) {
        this.setFeesPerUnitPerMonth(feesPerUnitPerMonth);
        return this;
    }

    public void setFeesPerUnitPerMonth(BigDecimal feesPerUnitPerMonth) {
        this.feesPerUnitPerMonth = feesPerUnitPerMonth;
    }

    public String getUnionHeadName() {
        return this.unionHeadName;
    }

    public Project unionHeadName(String unionHeadName) {
        this.setUnionHeadName(unionHeadName);
        return this;
    }

    public void setUnionHeadName(String unionHeadName) {
        this.unionHeadName = unionHeadName;
    }

    public String getUnionHeadMobileNumber() {
        return this.unionHeadMobileNumber;
    }

    public Project unionHeadMobileNumber(String unionHeadMobileNumber) {
        this.setUnionHeadMobileNumber(unionHeadMobileNumber);
        return this;
    }

    public void setUnionHeadMobileNumber(String unionHeadMobileNumber) {
        this.unionHeadMobileNumber = unionHeadMobileNumber;
    }

    public Integer getNumberOfUnits() {
        return this.numberOfUnits;
    }

    public Project numberOfUnits(Integer numberOfUnits) {
        this.setNumberOfUnits(numberOfUnits);
        return this;
    }

    public void setNumberOfUnits(Integer numberOfUnits) {
        this.numberOfUnits = numberOfUnits;
    }

    public String getConsentProvidedBy() {
        return this.consentProvidedBy;
    }

    public Project consentProvidedBy(String consentProvidedBy) {
        this.setConsentProvidedBy(consentProvidedBy);
        return this;
    }

    public void setConsentProvidedBy(String consentProvidedBy) {
        this.consentProvidedBy = consentProvidedBy;
    }

    public Instant getConsentProvidedOn() {
        return this.consentProvidedOn;
    }

    public Project consentProvidedOn(Instant consentProvidedOn) {
        this.setConsentProvidedOn(consentProvidedOn);
        return this;
    }

    public void setConsentProvidedOn(Instant consentProvidedOn) {
        this.consentProvidedOn = consentProvidedOn;
    }

    public String getCreatedBy() {
        return this.createdBy;
    }

    public Project createdBy(String createdBy) {
        this.setCreatedBy(createdBy);
        return this;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public Instant getCreatedDate() {
        return this.createdDate;
    }

    public Project createdDate(Instant createdDate) {
        this.setCreatedDate(createdDate);
        return this;
    }

    public void setCreatedDate(Instant createdDate) {
        this.createdDate = createdDate;
    }

    public String getLastModifiedBy() {
        return this.lastModifiedBy;
    }

    public Project lastModifiedBy(String lastModifiedBy) {
        this.setLastModifiedBy(lastModifiedBy);
        return this;
    }

    public void setLastModifiedBy(String lastModifiedBy) {
        this.lastModifiedBy = lastModifiedBy;
    }

    public Instant getLastModifiedDate() {
        return this.lastModifiedDate;
    }

    public Project lastModifiedDate(Instant lastModifiedDate) {
        this.setLastModifiedDate(lastModifiedDate);
        return this;
    }

    public void setLastModifiedDate(Instant lastModifiedDate) {
        this.lastModifiedDate = lastModifiedDate;
    }

    public Instant getDeletedDate() {
        return this.deletedDate;
    }

    public Project deletedDate(Instant deletedDate) {
        this.setDeletedDate(deletedDate);
        return this;
    }

    public void setDeletedDate(Instant deletedDate) {
        this.deletedDate = deletedDate;
    }

    public Set<SellerCommission> getSellerCommissions() {
        return this.sellerCommissions;
    }

    public void setSellerCommissions(Set<SellerCommission> sellerCommissions) {
        if (this.sellerCommissions != null) {
            this.sellerCommissions.forEach(i -> i.setProject(null));
        }
        if (sellerCommissions != null) {
            sellerCommissions.forEach(i -> i.setProject(this));
        }
        this.sellerCommissions = sellerCommissions;
    }

    public Project sellerCommissions(Set<SellerCommission> sellerCommissions) {
        this.setSellerCommissions(sellerCommissions);
        return this;
    }

    public Project addSellerCommissions(SellerCommission sellerCommission) {
        this.sellerCommissions.add(sellerCommission);
        sellerCommission.setProject(this);
        return this;
    }

    public Project removeSellerCommissions(SellerCommission sellerCommission) {
        this.sellerCommissions.remove(sellerCommission);
        sellerCommission.setProject(null);
        return this;
    }

    public Set<ProjectDiscount> getProjectDiscounts() {
        return this.projectDiscounts;
    }

    public void setProjectDiscounts(Set<ProjectDiscount> projectDiscounts) {
        if (this.projectDiscounts != null) {
            this.projectDiscounts.forEach(i -> i.setProject(null));
        }
        if (projectDiscounts != null) {
            projectDiscounts.forEach(i -> i.setProject(this));
        }
        this.projectDiscounts = projectDiscounts;
    }

    public Project projectDiscounts(Set<ProjectDiscount> projectDiscounts) {
        this.setProjectDiscounts(projectDiscounts);
        return this;
    }

    public Project addProjectDiscount(ProjectDiscount projectDiscount) {
        this.projectDiscounts.add(projectDiscount);
        projectDiscount.setProject(this);
        return this;
    }

    public Project removeProjectDiscount(ProjectDiscount projectDiscount) {
        this.projectDiscounts.remove(projectDiscount);
        projectDiscount.setProject(null);
        return this;
    }

    public Set<UnionMember> getUnionMembers() {
        return this.unionMembers;
    }

    public void setUnionMembers(Set<UnionMember> unionMembers) {
        if (this.unionMembers != null) {
            this.unionMembers.forEach(i -> i.setProject(null));
        }
        if (unionMembers != null) {
            unionMembers.forEach(i -> i.setProject(this));
        }
        this.unionMembers = unionMembers;
    }

    public Project unionMembers(Set<UnionMember> unionMembers) {
        this.setUnionMembers(unionMembers);
        return this;
    }

    public Project addUnionMembers(UnionMember unionMember) {
        this.unionMembers.add(unionMember);
        unionMember.setProject(this);
        return this;
    }

    public Project removeUnionMembers(UnionMember unionMember) {
        this.unionMembers.remove(unionMember);
        unionMember.setProject(null);
        return this;
    }

    public Set<Employee> getCollectors() {
        return this.collectors;
    }

    public void setCollectors(Set<Employee> employees) {
        if (this.collectors != null) {
            this.collectors.forEach(i -> i.setProject(null));
        }
        if (employees != null) {
            employees.forEach(i -> i.setProject(this));
        }
        this.collectors = employees;
    }

    public Project collectors(Set<Employee> employees) {
        this.setCollectors(employees);
        return this;
    }

    public Project addCollectors(Employee employee) {
        this.collectors.add(employee);
        employee.setProject(this);
        return this;
    }

    public Project removeCollectors(Employee employee) {
        this.collectors.remove(employee);
        employee.setProject(null);
        return this;
    }

    public Set<Block> getBlocks() {
        return this.blocks;
    }

    public void setBlocks(Set<Block> blocks) {
        if (this.blocks != null) {
            this.blocks.forEach(i -> i.setProject(null));
        }
        if (blocks != null) {
            blocks.forEach(i -> i.setProject(this));
        }
        this.blocks = blocks;
    }

    public Project blocks(Set<Block> blocks) {
        this.setBlocks(blocks);
        return this;
    }

    public Project addBlocks(Block block) {
        this.blocks.add(block);
        block.setProject(this);
        return this;
    }

    public Project removeBlocks(Block block) {
        this.blocks.remove(block);
        block.setProject(null);
        return this;
    }

    public Set<ExpenseType> getExpenseTypes() {
        return this.expenseTypes;
    }

    public void setExpenseTypes(Set<ExpenseType> expenseTypes) {
        if (this.expenseTypes != null) {
            this.expenseTypes.forEach(i -> i.setProject(null));
        }
        if (expenseTypes != null) {
            expenseTypes.forEach(i -> i.setProject(this));
        }
        this.expenseTypes = expenseTypes;
    }

    public Project expenseTypes(Set<ExpenseType> expenseTypes) {
        this.setExpenseTypes(expenseTypes);
        return this;
    }

    public Project addExpenseTypes(ExpenseType expenseType) {
        this.expenseTypes.add(expenseType);
        expenseType.setProject(this);
        return this;
    }

    public Project removeExpenseTypes(ExpenseType expenseType) {
        this.expenseTypes.remove(expenseType);
        expenseType.setProject(null);
        return this;
    }

    public Set<Notice> getNotices() {
        return this.notices;
    }

    public void setNotices(Set<Notice> notices) {
        if (this.notices != null) {
            this.notices.forEach(i -> i.setProject(null));
        }
        if (notices != null) {
            notices.forEach(i -> i.setProject(this));
        }
        this.notices = notices;
    }

    public Project notices(Set<Notice> notices) {
        this.setNotices(notices);
        return this;
    }

    public Project addNotices(Notice notice) {
        this.notices.add(notice);
        notice.setProject(this);
        return this;
    }

    public Project removeNotices(Notice notice) {
        this.notices.remove(notice);
        notice.setProject(null);
        return this;
    }

    public Set<ComplainType> getComplainTypes() {
        return this.complainTypes;
    }

    public void setComplainTypes(Set<ComplainType> complainTypes) {
        if (this.complainTypes != null) {
            this.complainTypes.forEach(i -> i.setProject(null));
        }
        if (complainTypes != null) {
            complainTypes.forEach(i -> i.setProject(this));
        }
        this.complainTypes = complainTypes;
    }

    public Project complainTypes(Set<ComplainType> complainTypes) {
        this.setComplainTypes(complainTypes);
        return this;
    }

    public Project addComplainTypes(ComplainType complainType) {
        this.complainTypes.add(complainType);
        complainType.setProject(this);
        return this;
    }

    public Project removeComplainTypes(ComplainType complainType) {
        this.complainTypes.remove(complainType);
        complainType.setProject(null);
        return this;
    }

    public Seller getSeller() {
        return this.seller;
    }

    public void setSeller(Seller seller) {
        this.seller = seller;
    }

    public Project seller(Seller seller) {
        this.setSeller(seller);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Project)) {
            return false;
        }
        return getId() != null && getId().equals(((Project) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Project{" +
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
            "}";
    }
}
