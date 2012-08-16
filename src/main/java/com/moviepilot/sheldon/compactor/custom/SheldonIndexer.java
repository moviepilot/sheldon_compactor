package com.moviepilot.sheldon.compactor.custom;

import com.moviepilot.sheldon.compactor.ModMapModifier;
import com.moviepilot.sheldon.compactor.config.Config;
import com.moviepilot.sheldon.compactor.event.IndexEntry;
import com.moviepilot.sheldon.compactor.event.PropertyContainerEvent;
import com.moviepilot.sheldon.compactor.handler.Indexer;
import com.moviepilot.sheldon.compactor.util.Progressor;
import com.moviepilot.sheldon.compactor.util.ProgressorHolder;
import gnu.trove.map.TObjectLongMap;

/**
 * @author stefanp
 * @since 06.08.2012
 */
public abstract class SheldonIndexer<E extends PropertyContainerEvent>
        implements Indexer<E>, ProgressorHolder, ModMapModifier {

    protected Progressor progressor;
    protected Config config;

    public void setup(final Config config) {
        this.config = config;
    }

    public void onEvent(final E event, final long sequence, final boolean endOfBatch) throws Exception {
        if (event.isOk()) {
            final String clazz          = getClassPropertyOf(event);
            final IndexEntry indexEntry = event.indexEntries[0];
            if (clazz != null) {
                final String trimmedClazz = clazz.trim();
                if (trimmedClazz.length() > 0)
                    addClassEntry(indexEntry, trimmedClazz);
            }

            if (hasXidProperty(event))
                addXidEntry(indexEntry, getXidPropertyOf(event));
        }
    }

    protected abstract void setupEntry(final IndexEntry indexEntry);

    public Progressor getProgressor() {
        return progressor;
    }

    public void setProgressor(final Progressor aProgressor) {
        progressor = aProgressor;
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

    private void addXidEntry(final IndexEntry entry, final long xid) {
        setupEntry(entry);
        entry.props.put(SheldonConstants.EXTERNAL_ID_KEY, xid);
        progressor.tick(getKind().lowercaseName() + "_index_xid");
    }

    private void addClassEntry(final IndexEntry entry, final String className) {
        setupEntry(entry);
        entry.props.put(SheldonConstants.CLASSNAME_KEY, className);
        progressor.tick(getKind().lowercaseName() + "_index_class");
    }

    public void modifyMap(final TObjectLongMap<String> modMap) {
        final Kind kind  = getKind();
        final String tag = kind.lowercaseName();
        modMap.put(tag + "_index_xid", config.getDotKind(kind));
        modMap.put(tag + "_index_class", config.getDotKind(kind));
    }
}
