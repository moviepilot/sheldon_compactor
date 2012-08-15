package com.moviepilot.sheldon.compactor.handler;

import com.lmax.disruptor.EventHandler;
import com.moviepilot.sheldon.compactor.util.ProgressReporter;
import com.moviepilot.sheldon.compactor.event.PropertyContainerEvent;
import gnu.trove.map.TObjectLongMap;

/**
 * AbstractPropertyContainerEventHandler
 *
 * @author stefanp
 * @since 05.08.12
 */
public abstract class AbstractPropertyContainerEventHandler<E extends PropertyContainerEvent>
    extends ProgressReporter
    implements PropertyContainerEventHandler<E> {

    public AbstractPropertyContainerEventHandler(final TObjectLongMap<String> modMap) {
        super(modMap);
    }

}
