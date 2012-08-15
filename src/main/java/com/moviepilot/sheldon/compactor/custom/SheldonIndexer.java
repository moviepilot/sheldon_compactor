package com.moviepilot.sheldon.compactor.custom;

import com.moviepilot.sheldon.compactor.event.IndexEntry;
import com.moviepilot.sheldon.compactor.event.PropertyContainerEvent;
import com.moviepilot.sheldon.compactor.handler.Indexer;

/**
 * @author stefanp
 * @since 06.08.2012
 */
public abstract class SheldonIndexer<E extends PropertyContainerEvent> implements Indexer<E> {

    public void onEvent(E event, long sequence, boolean endOfBatch) throws Exception {
        if (event.isOk()) {
            final String clazz = getClassPropertyOf(event);
            if (clazz != null) {
                final String trimmedClazz = clazz.trim();
                if (trimmedClazz.length() > 0) {
                    final IndexEntry entry = event.indexEntries[0];
                    entry.props.put(SheldonConstants.CLASSNAME_KEY, trimmedClazz);
                }
            }

            if (hasXidProperty(event)) {
                final IndexEntry entry = event.indexEntries[0];
                event.props.put(SheldonConstants.EXTERNAL_ID_KEY, getXidPropertyOf(event));
            }
        }
    }

    protected String getClassPropertyOf(E event) {
        if (event.props.containsKey(SheldonConstants.CLASSNAME_KEY))
            return (String) event.props.get(SheldonConstants.CLASSNAME_KEY);
        else
            return null;
    }

    protected boolean hasXidProperty(E event) {
        return event.props.containsKey(SheldonConstants.EXTERNAL_ID_KEY);
    }

    protected long getXidPropertyOf(E event) {
        final Object val = event.props.get(SheldonConstants.EXTERNAL_ID_KEY);
        if (val instanceof Long)
            return (Long) val;
        else
            return Long.parseLong(val.toString());
    }


}
