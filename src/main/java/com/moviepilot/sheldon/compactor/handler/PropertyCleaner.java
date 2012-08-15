package com.moviepilot.sheldon.compactor.handler;

import com.lmax.disruptor.EventHandler;
import com.moviepilot.sheldon.compactor.event.PropertyContainerEvent;
import com.moviepilot.sheldon.compactor.util.ProgressReporter;
import gnu.trove.map.TObjectLongMap;

/**
 * Handler for emptying out used events in order to avoid build up of garbage
 *
 * @author stefanp
 * @since 06.08.2012
 */
public class PropertyCleaner<E extends PropertyContainerEvent>
    extends ProgressReporter
    implements EventHandler<E> {

    public PropertyCleaner(final TObjectLongMap<String> modMap) {
        super(modMap);
    }

    @Override
    public void onEvent(E event, long sequence, boolean endOfBatch) throws Exception {
        event.clear(getProgressor());
    }
}
