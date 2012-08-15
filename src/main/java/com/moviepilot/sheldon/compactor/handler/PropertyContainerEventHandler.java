package com.moviepilot.sheldon.compactor.handler;

import com.lmax.disruptor.EventHandler;
import com.moviepilot.sheldon.compactor.event.PropertyContainerEvent;

/**
 * @author stefanp
 * @since 08.08.12
 */
public interface PropertyContainerEventHandler<E extends PropertyContainerEvent> extends EventHandler<E> {
}
