package com.moviepilot.sheldon.compactor.handler;

import com.lmax.disruptor.EventHandler;
import com.moviepilot.sheldon.compactor.custom.Toolbox;
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

    public final void onEvent(final E event, final long sequence, final boolean endOfBatch) throws Exception {
        try {
            onEvent_(event, sequence, endOfBatch);
        }
        catch (Exception e) {
            System.err.println("Error in " + getClass());
            Toolbox.printException(e);
            throw e;
        }
    }

    public void onEvent_(final E event, final long sequence, final boolean endOfBatch) throws Exception {
        event.clear(getProgressor());
    }
}
