package com.moviepilot.sheldon.compactor.event;

import com.lmax.disruptor.EventFactory;
import com.moviepilot.sheldon.compactor.config.Config;

/**
 * NodeEvent
 *
 * @author stefanp
 * @since 03.08.12
 */
public final class NodeEvent extends PropertyContainerEvent {

    public boolean referenceNode = false;

    public NodeEvent(final Config config) {
        super(config);
    }

    final public static class Factory extends PropertyContainerEventFactory<NodeEvent> {

        public Factory(final Config config) {
            super(config);
        }

        public NodeEvent newInstance() {
            return new NodeEvent(config);
        }
    }
}

