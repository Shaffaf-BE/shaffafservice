package com.shaffaf.shaffafservice.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class SellerCommissionTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));
    private static final AtomicInteger intCount = new AtomicInteger(random.nextInt() + (2 * Short.MAX_VALUE));

    public static SellerCommission getSellerCommissionSample1() {
        return new SellerCommission()
            .id(1L)
            .commissionMonth(1)
            .commissionYear(1)
            .commissionPaidBy("commissionPaidBy1")
            .phoneNumber("phoneNumber1")
            .createdBy("createdBy1")
            .lastModifiedBy("lastModifiedBy1");
    }

    public static SellerCommission getSellerCommissionSample2() {
        return new SellerCommission()
            .id(2L)
            .commissionMonth(2)
            .commissionYear(2)
            .commissionPaidBy("commissionPaidBy2")
            .phoneNumber("phoneNumber2")
            .createdBy("createdBy2")
            .lastModifiedBy("lastModifiedBy2");
    }

    public static SellerCommission getSellerCommissionRandomSampleGenerator() {
        return new SellerCommission()
            .id(longCount.incrementAndGet())
            .commissionMonth(intCount.incrementAndGet())
            .commissionYear(intCount.incrementAndGet())
            .commissionPaidBy(UUID.randomUUID().toString())
            .phoneNumber(UUID.randomUUID().toString())
            .createdBy(UUID.randomUUID().toString())
            .lastModifiedBy(UUID.randomUUID().toString());
    }
}
