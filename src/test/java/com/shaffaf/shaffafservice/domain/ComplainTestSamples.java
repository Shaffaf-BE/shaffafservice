package com.shaffaf.shaffafservice.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class ComplainTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    public static Complain getComplainSample1() {
        return new Complain()
            .id(1L)
            .title("title1")
            .description("description1")
            .addedBy("addedBy1")
            .assignee("assignee1")
            .resolutionComments("resolutionComments1")
            .resolvedBy("resolvedBy1")
            .createdBy("createdBy1")
            .lastModifiedBy("lastModifiedBy1");
    }

    public static Complain getComplainSample2() {
        return new Complain()
            .id(2L)
            .title("title2")
            .description("description2")
            .addedBy("addedBy2")
            .assignee("assignee2")
            .resolutionComments("resolutionComments2")
            .resolvedBy("resolvedBy2")
            .createdBy("createdBy2")
            .lastModifiedBy("lastModifiedBy2");
    }

    public static Complain getComplainRandomSampleGenerator() {
        return new Complain()
            .id(longCount.incrementAndGet())
            .title(UUID.randomUUID().toString())
            .description(UUID.randomUUID().toString())
            .addedBy(UUID.randomUUID().toString())
            .assignee(UUID.randomUUID().toString())
            .resolutionComments(UUID.randomUUID().toString())
            .resolvedBy(UUID.randomUUID().toString())
            .createdBy(UUID.randomUUID().toString())
            .lastModifiedBy(UUID.randomUUID().toString());
    }
}
