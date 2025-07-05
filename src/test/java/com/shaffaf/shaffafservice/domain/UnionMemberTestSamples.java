package com.shaffaf.shaffafservice.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class UnionMemberTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    public static UnionMember getUnionMemberSample1() {
        return new UnionMember()
            .id(1L)
            .firstName("firstName1")
            .lastName("lastName1")
            .email("email1")
            .phoneNumber("+923311234567")
            .createdBy("createdBy1")
            .lastModifiedBy("lastModifiedBy1")
            .isUnionHead(true);
    }

    public static UnionMember getUnionMemberSample2() {
        return new UnionMember()
            .id(2L)
            .firstName("firstName2")
            .lastName("lastName2")
            .email("email2")
            .phoneNumber("+923327654321")
            .createdBy("createdBy2")
            .lastModifiedBy("lastModifiedBy2")
            .isUnionHead(false);
    }

    public static UnionMember getUnionMemberRandomSampleGenerator() {
        return new UnionMember()
            .id(longCount.incrementAndGet())
            .firstName(UUID.randomUUID().toString())
            .lastName(UUID.randomUUID().toString())
            .email(UUID.randomUUID().toString())
            .phoneNumber("+9233" + String.format("%08d", random.nextInt(100000000)))
            .createdBy(UUID.randomUUID().toString())
            .lastModifiedBy(UUID.randomUUID().toString())
            .isUnionHead(random.nextBoolean());
    }
}
