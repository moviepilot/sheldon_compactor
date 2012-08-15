package com.moviepilot.sheldon.compactor.handler;

import com.moviepilot.sheldon.compactor.config.Config;
import com.moviepilot.sheldon.compactor.event.PropertyContainerEvent;

/**
 * Handler for determining what needs to be indexed
 *
 * @author stefanp
 * @since 09.08.12
 */
public interface Indexer<E extends PropertyContainerEvent> extends PropertyContainerEventHandler<E> {

    public void setup(final Config config);
    public void flush();
}
