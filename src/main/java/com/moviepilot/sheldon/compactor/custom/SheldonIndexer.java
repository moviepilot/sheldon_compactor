package com.moviepilot.sheldon.compactor.custom;

import com.moviepilot.sheldon.compactor.ModMapModifier;
import com.moviepilot.sheldon.compactor.config.Config;
import com.moviepilot.sheldon.compactor.event.IndexEntry;
import com.moviepilot.sheldon.compactor.event.IndexSwapper;
import com.moviepilot.sheldon.compactor.event.PropertyContainerEvent;
import com.moviepilot.sheldon.compactor.handler.Indexer;
import com.moviepilot.sheldon.compactor.util.Progressor;
import com.moviepilot.sheldon.compactor.util.ProgressorHolder;
import gnu.trove.map.TObjectLongMap;

import static com.moviepilot.sheldon.compactor.event.PropertyContainerEvent.Action;

/**
 * @author stefanp
 * @since 06.08.2012
 */
public abstract class SheldonIndexer<E extends PropertyContainerEvent>
        implements Indexer<E>, ProgressorHolder, ModMapModifier {

    protected Progressor progressor;
    protected Config config;
    protected IndexSwapper swapper;

    public void setup(final Config config) {
        this.config = config;
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

    public void onEvent_(final E event, final long sequence, final boolean endOfBatch) throws Exception {
        if (event.isOk() && (event.action != Action.DELETE)) {
            final String clazz          = getClassPropertyOf(event);
            final IndexEntry indexEntry = event.indexEntries[0];
            if (clazz != null) {
                final String trimmedClazz = clazz.trim();
                if (trimmedClazz.length() > 0) {
                    swapper.onEntry(indexEntry, sequence, endOfBatch);
                    addClassEntry(indexEntry, trimmedClazz);
                }
            }

            if (hasXidProperty(event)) {
                swapper.onEntry(indexEntry, sequence, endOfBatch);
                addXidEntry(indexEntry, getXidPropertyOf(event));
            }
        }
    }

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
        entry.props.put(SheldonConstants.EXTERNAL_ID_KEY, xid);
        progressor.tick(getXidTag());
    }

    private void addClassEntry(final IndexEntry entry, final String className) {
        entry.props.put(SheldonConstants.CLASSNAME_KEY, className);
        progressor.tick(getClassTag());
    }

    private String getXidTag() {
        return getKind().lowercaseName() + "_index_xid";
    }

    private String getClassTag() {
        return getKind().lowercaseName() + "_index_class";
    }

    public void flush() {
        swapper.flush();
    }

    public void modifyMap(final TObjectLongMap<String> modMap) {
        final Kind kind  = getKind();
        modMap.put(getXidTag(), config.getDotKind(kind));
        modMap.put(getClassTag(), config.getDotKind(kind));
    }
}
