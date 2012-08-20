package com.moviepilot.sheldon.compactor.event;

import com.moviepilot.sheldon.compactor.config.Config;
import gnu.trove.map.TMap;
import gnu.trove.map.hash.THashMap;
import org.neo4j.unsafe.batchinsert.BatchInserterIndex;

/**
 * @author stefanp
 * @since 09.08.12
 */
public class IndexEntry {
    enum Mode {
        ADD, UPDATE_OR_ADD;

        public void write(final BatchInserterIndex index, final long id, final TMap<String,Object> props) {
            if (props.size() > 0)
                switch (this) {
                    case ADD: index.add(id, props); return;
                    case UPDATE_OR_ADD: index.updateOrAdd(id, props); return;
                    default:
                        throw new IllegalStateException("Should never be reached");
                }
        }
    }

    public Mode mode = Mode.ADD;

    public BatchInserterIndex index;
    public TMap<String, Object> props;

    public int numIndex  = 0;
    public boolean flush = false;

    public IndexEntry(final Config config) {
        props = new THashMap<String, Object>(config.getNumIndexProps());
    }

    public boolean write(long id) {
        if (index == null)
            return false;

        mode.write(index, id, props);

        return true;
    }

    public void clear() {
        mode     = Mode.ADD;
        index    = null;
        numIndex = 0;
        flush    = false;
        props.clear();
    }
}
