package com.moviepilot.sheldon.compactor.producer.glops;

import com.moviepilot.sheldon.compactor.config.Config;
import com.moviepilot.sheldon.compactor.event.PropertyContainerEvent;
import com.moviepilot.sheldon.compactor.producer.PropertyContainerEventProducer;
import org.neo4j.graphdb.GraphDatabaseService;

/**
 * @author stefanp
 * @since 22.08.12
 */
public abstract class GlopsEventProducer<E extends PropertyContainerEvent> extends PropertyContainerEventProducer<E> {
    protected final Config config;
    protected final GraphDatabaseService src;

    public GlopsEventProducer(final Config config, final GraphDatabaseService src) {
        this.config = config;
        this.src    = src;
    }
}
