package com.moviepilot.sheldon.compactor.handler;

import com.moviepilot.sheldon.compactor.config.Config;
import com.moviepilot.sheldon.compactor.event.IndexEntry;
import com.moviepilot.sheldon.compactor.event.PropertyContainerEvent;

import static com.moviepilot.sheldon.compactor.event.PropertyContainerEvent.Action.DELETE;

/**
 * Handler for writing into the index
 *
 * @author stefanp
 * @since 06.08.2012
 */
public final class IndexWriter<E extends PropertyContainerEvent> extends AbstractPropertyContainerEventHandler<E> {

    private final Kind kind;
    private final int indexId;
    private final String tag;

    public IndexWriter(final Config config, final Kind kind, final int indexId) {
        super(config.getModMap());
        this.kind    = kind;
        this.indexId = indexId;
        this.tag     = "index_writer_" + indexId + "_flush";
    }

    public void onEvent(E event, long sequence, boolean endOfBatch) throws Exception {
        if (event.isOk() && (event.action != DELETE)) {
            for (final IndexEntry entry : event.indexEntries)
                if (entry.numIndex == indexId) {
                    if (entry.write(event.id) && (entry.flush))
                        getProgressor().tick(tag);
                }
        }
    }

    public Kind getKind() {
        return kind;
    }
}
