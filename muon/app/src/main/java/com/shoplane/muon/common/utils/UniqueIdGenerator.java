package com.shoplane.muon.common.utils;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by ravmon on 1/10/15.
 */
public class UniqueIdGenerator {


    private static UniqueIdGenerator uniqueIdGeneratorInstance;

    private AtomicInteger seq;

    private UniqueIdGenerator() {
        seq = new AtomicInteger(1);
    }

    public int getUniqueId() {
        return seq.incrementAndGet();
    }

    public static synchronized UniqueIdGenerator getInstance() {
        if (null == uniqueIdGeneratorInstance) {
            uniqueIdGeneratorInstance = new UniqueIdGenerator();
        }
        return uniqueIdGeneratorInstance;
    }

}
