package com.shaffaf.shaffafservice.service.dto;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

/**
 * DTO for Dashboard Data containing aggregated financial information and transaction details.
 */
public class DashboardDataDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    private BigDecimal totalPayment;
    private Long totalSellers;
    private BigDecimal totalSales;
    private Long newSellers;
    private BigDecimal totalDues;
    private List<TransactionDetailDTO> transactionDetails;
    private Long totalTransactions;
    private Integer currentPage;
    private Integer totalPages;

    public DashboardDataDTO() {}

    public DashboardDataDTO(
        BigDecimal totalPayment,
        Long totalSellers,
        BigDecimal totalSales,
        Long newSellers,
        BigDecimal totalDues,
        List<TransactionDetailDTO> transactionDetails,
        Long totalTransactions,
        Integer currentPage,
        Integer totalPages
    ) {
        this.totalPayment = totalPayment;
        this.totalSellers = totalSellers;
        this.totalSales = totalSales;
        this.newSellers = newSellers;
        this.totalDues = totalDues;
        this.transactionDetails = transactionDetails;
        this.totalTransactions = totalTransactions;
        this.currentPage = currentPage;
        this.totalPages = totalPages;
    }

    // Getters and Setters
    public BigDecimal getTotalPayment() {
        return totalPayment;
    }

    public void setTotalPayment(BigDecimal totalPayment) {
        this.totalPayment = totalPayment;
    }

    public Long getTotalSellers() {
        return totalSellers;
    }

    public void setTotalSellers(Long totalSellers) {
        this.totalSellers = totalSellers;
    }

    public BigDecimal getTotalSales() {
        return totalSales;
    }

    public void setTotalSales(BigDecimal totalSales) {
        this.totalSales = totalSales;
    }

    public Long getNewSellers() {
        return newSellers;
    }

    public void setNewSellers(Long newSellers) {
        this.newSellers = newSellers;
    }

    public BigDecimal getTotalDues() {
        return totalDues;
    }

    public void setTotalDues(BigDecimal totalDues) {
        this.totalDues = totalDues;
    }

    public List<TransactionDetailDTO> getTransactionDetails() {
        return transactionDetails;
    }

    public void setTransactionDetails(List<TransactionDetailDTO> transactionDetails) {
        this.transactionDetails = transactionDetails;
    }

    public Long getTotalTransactions() {
        return totalTransactions;
    }

    public void setTotalTransactions(Long totalTransactions) {
        this.totalTransactions = totalTransactions;
    }

    public Integer getCurrentPage() {
        return currentPage;
    }

    public void setCurrentPage(Integer currentPage) {
        this.currentPage = currentPage;
    }

    public Integer getTotalPages() {
        return totalPages;
    }

    public void setTotalPages(Integer totalPages) {
        this.totalPages = totalPages;
    }

    @Override
    public String toString() {
        return (
            "DashboardDataDTO{" +
            "totalPayment=" +
            totalPayment +
            ", totalSellers=" +
            totalSellers +
            ", totalSales=" +
            totalSales +
            ", newSellers=" +
            newSellers +
            ", totalDues=" +
            totalDues +
            ", totalTransactions=" +
            totalTransactions +
            ", currentPage=" +
            currentPage +
            ", totalPages=" +
            totalPages +
            '}'
        );
    }
}
