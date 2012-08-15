package com.moviepilot.sheldon.compactor.event;

import com.lmax.disruptor.EventFactory;
import com.moviepilot.sheldon.compactor.config.Config;

/**
 * PropertyContainerEventFactory
 *
 * @author stefanp
 * @since 05.08.12
 */
public abstract class PropertyContainerEventFactory<E extends PropertyContainerEvent> implements EventFactory<E> {
    protected final Config config;

    public PropertyContainerEventFactory(final Config config) {
        this.config = config;
    }
}
