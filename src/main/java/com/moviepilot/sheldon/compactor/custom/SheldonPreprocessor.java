package com.moviepilot.sheldon.compactor.custom;

import com.moviepilot.sheldon.compactor.event.PropertyContainerEvent;
import com.moviepilot.sheldon.compactor.handler.PropertyContainerEventHandler;
import com.moviepilot.sheldon.compactor.util.Progressor;
import com.moviepilot.sheldon.compactor.util.ProgressorHolder;

/**
 * @author stefanp
 * @since 08.08.12
 */
public abstract class SheldonPreprocessor<E extends PropertyContainerEvent>
        implements PropertyContainerEventHandler<E>, ProgressorHolder {

    private Progressor progressor;

    public final void onEvent(E event, long sequence, boolean endOfBatch) throws Exception {
        if (event.isOk()) {
            if (event.props.containsKey(SheldonConstants.EXTERNAL_ID_KEY)) {
                final Object value  = event.props.get(SheldonConstants.EXTERNAL_ID_KEY);
                final Long newValue = value instanceof Long ? ((Long)value) : Long.parseLong(value.toString());
                event.props.put(SheldonConstants.EXTERNAL_ID_KEY, newValue);
            }
            onOkEvent(event, sequence, endOfBatch);
        }
    }

    protected abstract void onOkEvent(E event, long sequence, boolean endOfBatch);

    public Progressor getProgressor() {
        return progressor;
    }

    public void setProgressor(Progressor progressor) {
        this.progressor = progressor;
    }
}
