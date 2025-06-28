package com.shaffaf.shaffafservice.service.dto;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.Instant;

/**
 * DTO for Transaction Detail information.
 */
public class TransactionDetailDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;
    private String sellerName;
    private String sellerPhoneNumber;
    private String projectName;
    private BigDecimal amount;
    private Integer numberOfUnits;
    private BigDecimal feesPerUnit;
    private Instant transactionDate;
    private String status;
    private String description;

    public TransactionDetailDTO() {}

    public TransactionDetailDTO(
        Long id,
        String sellerName,
        String sellerPhoneNumber,
        String projectName,
        BigDecimal amount,
        Integer numberOfUnits,
        BigDecimal feesPerUnit,
        Instant transactionDate,
        String status,
        String description
    ) {
        this.id = id;
        this.sellerName = sellerName;
        this.sellerPhoneNumber = sellerPhoneNumber;
        this.projectName = projectName;
        this.amount = amount;
        this.numberOfUnits = numberOfUnits;
        this.feesPerUnit = feesPerUnit;
        this.transactionDate = transactionDate;
        this.status = status;
        this.description = description;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getSellerName() {
        return sellerName;
    }

    public void setSellerName(String sellerName) {
        this.sellerName = sellerName;
    }

    public String getSellerPhoneNumber() {
        return sellerPhoneNumber;
    }

    public void setSellerPhoneNumber(String sellerPhoneNumber) {
        this.sellerPhoneNumber = sellerPhoneNumber;
    }

    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public Integer getNumberOfUnits() {
        return numberOfUnits;
    }

    public void setNumberOfUnits(Integer numberOfUnits) {
        this.numberOfUnits = numberOfUnits;
    }

    public BigDecimal getFeesPerUnit() {
        return feesPerUnit;
    }

    public void setFeesPerUnit(BigDecimal feesPerUnit) {
        this.feesPerUnit = feesPerUnit;
    }

    public Instant getTransactionDate() {
        return transactionDate;
    }

    public void setTransactionDate(Instant transactionDate) {
        this.transactionDate = transactionDate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public String toString() {
        return (
            "TransactionDetailDTO{" +
            "id=" +
            id +
            ", sellerName='" +
            sellerName +
            '\'' +
            ", sellerPhoneNumber='" +
            sellerPhoneNumber +
            '\'' +
            ", projectName='" +
            projectName +
            '\'' +
            ", amount=" +
            amount +
            ", numberOfUnits=" +
            numberOfUnits +
            ", feesPerUnit=" +
            feesPerUnit +
            ", transactionDate=" +
            transactionDate +
            ", status='" +
            status +
            '\'' +
            ", description='" +
            description +
            '\'' +
            '}'
        );
    }
}
