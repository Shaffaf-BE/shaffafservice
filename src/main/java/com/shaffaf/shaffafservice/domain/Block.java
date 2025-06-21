package com.shaffaf.shaffafservice.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.time.Instant;
import java.util.HashSet;
import java.util.Set;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * A Block.
 */
@Entity
@Table(name = "block")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@SuppressWarnings("common-java:DuplicatedBlocks")
public class Block implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator", sequenceName = "block_seq")
    @Column(name = "id")
    private Long id;

    @NotNull
    @Column(name = "name", nullable = false)
    private String name;

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

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "block")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @JsonIgnoreProperties(value = { "residents", "feesCollections", "unitType", "block" }, allowSetters = true)
    private Set<Unit> units = new HashSet<>();

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "block")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @JsonIgnoreProperties(value = { "feesCollections", "unitType", "block" }, allowSetters = true)
    private Set<FeesConfiguration> feesConfigurations = new HashSet<>();

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

    public Block id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return this.name;
    }

    public Block name(String name) {
        this.setName(name);
        return this;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCreatedBy() {
        return this.createdBy;
    }

    public Block createdBy(String createdBy) {
        this.setCreatedBy(createdBy);
        return this;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public Instant getCreatedDate() {
        return this.createdDate;
    }

    public Block createdDate(Instant createdDate) {
        this.setCreatedDate(createdDate);
        return this;
    }

    public void setCreatedDate(Instant createdDate) {
        this.createdDate = createdDate;
    }

    public String getLastModifiedBy() {
        return this.lastModifiedBy;
    }

    public Block lastModifiedBy(String lastModifiedBy) {
        this.setLastModifiedBy(lastModifiedBy);
        return this;
    }

    public void setLastModifiedBy(String lastModifiedBy) {
        this.lastModifiedBy = lastModifiedBy;
    }

    public Instant getLastModifiedDate() {
        return this.lastModifiedDate;
    }

    public Block lastModifiedDate(Instant lastModifiedDate) {
        this.setLastModifiedDate(lastModifiedDate);
        return this;
    }

    public void setLastModifiedDate(Instant lastModifiedDate) {
        this.lastModifiedDate = lastModifiedDate;
    }

    public Instant getDeletedOn() {
        return this.deletedOn;
    }

    public Block deletedOn(Instant deletedOn) {
        this.setDeletedOn(deletedOn);
        return this;
    }

    public void setDeletedOn(Instant deletedOn) {
        this.deletedOn = deletedOn;
    }

    public Set<Unit> getUnits() {
        return this.units;
    }

    public void setUnits(Set<Unit> units) {
        if (this.units != null) {
            this.units.forEach(i -> i.setBlock(null));
        }
        if (units != null) {
            units.forEach(i -> i.setBlock(this));
        }
        this.units = units;
    }

    public Block units(Set<Unit> units) {
        this.setUnits(units);
        return this;
    }

    public Block addUnits(Unit unit) {
        this.units.add(unit);
        unit.setBlock(this);
        return this;
    }

    public Block removeUnits(Unit unit) {
        this.units.remove(unit);
        unit.setBlock(null);
        return this;
    }

    public Set<FeesConfiguration> getFeesConfigurations() {
        return this.feesConfigurations;
    }

    public void setFeesConfigurations(Set<FeesConfiguration> feesConfigurations) {
        if (this.feesConfigurations != null) {
            this.feesConfigurations.forEach(i -> i.setBlock(null));
        }
        if (feesConfigurations != null) {
            feesConfigurations.forEach(i -> i.setBlock(this));
        }
        this.feesConfigurations = feesConfigurations;
    }

    public Block feesConfigurations(Set<FeesConfiguration> feesConfigurations) {
        this.setFeesConfigurations(feesConfigurations);
        return this;
    }

    public Block addFeesConfiguration(FeesConfiguration feesConfiguration) {
        this.feesConfigurations.add(feesConfiguration);
        feesConfiguration.setBlock(this);
        return this;
    }

    public Block removeFeesConfiguration(FeesConfiguration feesConfiguration) {
        this.feesConfigurations.remove(feesConfiguration);
        feesConfiguration.setBlock(null);
        return this;
    }

    public Project getProject() {
        return this.project;
    }

    public void setProject(Project project) {
        this.project = project;
    }

    public Block project(Project project) {
        this.setProject(project);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Block)) {
            return false;
        }
        return getId() != null && getId().equals(((Block) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Block{" +
            "id=" + getId() +
            ", name='" + getName() + "'" +
            ", createdBy='" + getCreatedBy() + "'" +
            ", createdDate='" + getCreatedDate() + "'" +
            ", lastModifiedBy='" + getLastModifiedBy() + "'" +
            ", lastModifiedDate='" + getLastModifiedDate() + "'" +
            ", deletedOn='" + getDeletedOn() + "'" +
            "}";
    }
}
