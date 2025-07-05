package com.shaffaf.shaffafservice.service.dto;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

/**
 * DTO for Seller Sales Dashboard with aggregated data and individual seller breakdowns.
 */
public class SellerSalesDashboardDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    // Dashboard aggregates (same as DashboardDataDTO)
    private BigDecimal totalPayment;
    private Long totalSellers;
    private BigDecimal totalSales;
    private Long newSellers;
    private BigDecimal totalDues;

    // Seller-specific aggregated sales data
    private List<SellerSalesAggregateDTO> sellerSalesData;

    // Pagination info
    private Long totalElements;
    private Integer pageNumber;
    private Integer totalPages;

    public SellerSalesDashboardDTO() {}

    public SellerSalesDashboardDTO(
        BigDecimal totalPayment,
        Long totalSellers,
        BigDecimal totalSales,
        Long newSellers,
        BigDecimal totalDues,
        List<SellerSalesAggregateDTO> sellerSalesData,
        Long totalElements,
        Integer pageNumber,
        Integer totalPages
    ) {
        this.totalPayment = totalPayment;
        this.totalSellers = totalSellers;
        this.totalSales = totalSales;
        this.newSellers = newSellers;
        this.totalDues = totalDues;
        this.sellerSalesData = sellerSalesData;
        this.totalElements = totalElements;
        this.pageNumber = pageNumber;
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

    public List<SellerSalesAggregateDTO> getSellerSalesData() {
        return sellerSalesData;
    }

    public void setSellerSalesData(List<SellerSalesAggregateDTO> sellerSalesData) {
        this.sellerSalesData = sellerSalesData;
    }

    public Long getTotalElements() {
        return totalElements;
    }

    public void setTotalElements(Long totalElements) {
        this.totalElements = totalElements;
    }

    public Integer getPageNumber() {
        return pageNumber;
    }

    public void setPageNumber(Integer pageNumber) {
        this.pageNumber = pageNumber;
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
            "SellerSalesDashboardDTO{" +
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
            ", sellerSalesData=" +
            (sellerSalesData != null ? sellerSalesData.size() + " items" : "null") +
            ", totalElements=" +
            totalElements +
            ", pageNumber=" +
            pageNumber +
            ", totalPages=" +
            totalPages +
            '}'
        );
    }
}
