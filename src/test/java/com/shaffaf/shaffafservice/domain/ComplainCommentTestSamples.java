package com.shaffaf.shaffafservice.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class ComplainCommentTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    public static ComplainComment getComplainCommentSample1() {
        return new ComplainComment()
            .id(1L)
            .comment("comment1")
            .addedBy("addedBy1")
            .createdBy("createdBy1")
            .lastModifiedBy("lastModifiedBy1");
    }

    public static ComplainComment getComplainCommentSample2() {
        return new ComplainComment()
            .id(2L)
            .comment("comment2")
            .addedBy("addedBy2")
            .createdBy("createdBy2")
            .lastModifiedBy("lastModifiedBy2");
    }

    public static ComplainComment getComplainCommentRandomSampleGenerator() {
        return new ComplainComment()
            .id(longCount.incrementAndGet())
            .comment(UUID.randomUUID().toString())
            .addedBy(UUID.randomUUID().toString())
            .createdBy(UUID.randomUUID().toString())
            .lastModifiedBy(UUID.randomUUID().toString());
    }
}
