package com.moviepilot.sheldon.compactor.event;

import com.moviepilot.sheldon.compactor.config.Config;
import org.neo4j.unsafe.batchinsert.BatchInserterIndex;

/**
 * IndexSwapper
 *
 * @author stefanp
 * @since 20.08.2012
 */
public abstract class IndexSwapper {

    private final int batchSize;
    private final long[] counts;
    private final BatchInserterIndex[] indices;
    private final long minIndex;
    private final long maxIndex;

    public abstract BatchInserterIndex makeNew();

    public IndexSwapper(final Config config) {
        this(config.getNumIndexWriters(),
             config.getIndexBatchSize(),
             config.getIndexFlushMinInterval(),
             config.getIndexFlushMaxInterval());
    }

    public IndexSwapper(final int numIndices, final int batchSize, final long minIndex, final long maxIndex) {
        this.batchSize = batchSize;
        this.indices   = new BatchInserterIndex[numIndices];
        this.minIndex  = minIndex;
        this.maxIndex  = maxIndex;
        this.counts    = new long[numIndices];
        for (int i = 0; i < numIndices; i++) {
            indices[i] = makeNew();
            counts[i]  = 0L;
        }

    }

    public void onEntry(final IndexEntry entry, final long sequence, final boolean endOfBatch) {
        entry.numIndex     = (int) ((sequence / batchSize) % indices.length);
        entry.index        = indices[entry.numIndex];
        final long step    = counts[entry.numIndex] % batchSize;
        if ( ((step >= minIndex) && endOfBatch) || (step >= maxIndex) ) {
            entry.flush            = true;
            counts[entry.numIndex] = 0;
        }
        else {
            entry.flush             = false;
            counts[entry.numIndex] += 1;
        }
    }

    public void flush() {
        for (int i = 0; i < indices.length; i++)
            if (counts[i] > 0)
                indices[i].flush();
    }
}
