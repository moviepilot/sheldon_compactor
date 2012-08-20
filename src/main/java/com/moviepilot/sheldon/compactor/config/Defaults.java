package com.moviepilot.sheldon.compactor.config;

/**
 * Defaults
 *
 * Global configuration parameters used by compactor
  *
 * @author stefanp
 * @since 05.08.12
 */
public class Defaults {
    /**
     * Size of disruptor ring buffer used
     */
    public static final int RING_SIZE = 18;

    /**
     * Number of events that have to be processed before flushing the index
     */
    public static final int INDEX_FLUSH_MIN_INTERVAL = 12;

    /**
     * Number of events that may get processed before the index must be flushed
     */
    public static final int INDEX_FLUSH_MAX_INTERVAL = 14;

    /**
     * Default number of counts expected to be used by a Progressor
     */
    public static final int DEFAULT_NUM_COUNTS = 12;

    /**
     * Default number of properties on a node or an edge
     */
    public static final int DEFAULT_NUM_PROPS = 32;

    /**
     * Default number of properties that are indexed
     */
    public static final int DEFAULT_NUM_INDEX_ENTRIES =  1;

    /**
     * Default number of properties that are indexed
     */
    public static final int DEFAULT_NUM_INDEX_PROPS =  2;

    /**
     * Report progress every DOT_OK events cleared
     */
    public static final int DOT_OK = 50000;

    /**
     * Report progress every DOT_NODES nodes written
     */
    public static final int DOT_NODES = 10000;

    /**
     * Report progress every DOT_EDGES edges written
     */
    public static final int DOT_EDGES = 100000;
}
