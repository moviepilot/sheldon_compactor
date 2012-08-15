package com.moviepilot.sheldon.compactor.event;

import com.moviepilot.sheldon.compactor.config.Config;
import com.moviepilot.sheldon.compactor.custom.SheldonConstants;
import com.moviepilot.sheldon.compactor.util.Progressor;
import gnu.trove.map.TMap;
import gnu.trove.map.hash.THashMap;

/**
 * PropertyContainerEvent
 *
 * Superclass of all events handled by the compactor
 *
 * @author stefanp
 * @since 05.08.12
 */
public class PropertyContainerEvent {

    public static enum Action {
        CREATE, DELETE, UPDATE
    }

    public Action action;

    public Exception failure;

    public static TMap<String, Object> makeNewPropertyMap(final Config config) {
        return new THashMap<String, Object>(config.getNumProps());
    }

    public static IndexEntry[] makeNewIndexEntrySet(final Config config) {
        final int numIndexEntries  = config.getNumIndexEntries();
        final IndexEntry[] entries = new IndexEntry[numIndexEntries];
        for (int i = 0; i < entries.length; i++)
            entries[i] = new IndexEntry(config);
        return entries;
    }

    /**
     * Properties read from the PropertyContainer
     */
    public final TMap<String, Object> props;

    /**
     * Id of the PropertyContainer
     */
    public long id;

    public IndexEntry[] indexEntries;

    public PropertyContainerEvent(final Config config) {
        props        = makeNewPropertyMap(config);
        indexEntries = makeNewIndexEntrySet(config);
        reset();
    }

    public boolean isOk() {
        return failure == null;
    }

    public TMap<String, Object> getProps() {
        return props.size() == 0 ? null : props;
    }

    public void clear(final Progressor progressor) {
        if (failure == null)
            progressor.tick("clean_ok");
        else {
            try {
                System.err.println(toString());
            }
            catch (RuntimeException e) {
                System.err.println(e);  // gulp
            }
            progressor.tick("clean_failed");
        }
        reset();
    }

    protected void reset() {
        props.clear();

        for (final IndexEntry indexEntry : indexEntries) {
            indexEntry.index = null;
        }

        action  = null;
        failure = null;
    }

    @Override
    public String toString() {
        return getClass().getName() +
               "{" +
                "id=" + id +
                ", props=" + props +
                ", action=" + action +
                ", failure=" + failure +
                '}';
    }
}
