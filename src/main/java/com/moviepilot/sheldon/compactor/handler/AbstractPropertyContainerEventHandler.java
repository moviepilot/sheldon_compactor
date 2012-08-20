package com.moviepilot.sheldon.compactor.handler;

import com.moviepilot.sheldon.compactor.custom.Toolbox;
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

    public final void onEvent(E event, long sequence, boolean endOfBatch) throws Exception {
        try {
            onEvent_(event, sequence, endOfBatch);
        }
        catch (Exception e) {
            System.err.println("Error in " + getClass());
            Toolbox.printException(e);
            throw e;
        }
    }

    public abstract void onEvent_(E event, long sequence, boolean endOfBatch) throws Exception;

}
