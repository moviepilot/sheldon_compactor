package com.moviepilot.sheldon.compactor.custom;

import com.moviepilot.sheldon.compactor.config.Config;
import com.moviepilot.sheldon.compactor.event.EdgeEvent;
import com.moviepilot.sheldon.compactor.event.IndexEntry;
import com.moviepilot.sheldon.compactor.event.IndexSwapper;
import com.moviepilot.sheldon.compactor.handler.EdgeIndexer;
import org.neo4j.helpers.collection.MapUtil;
import org.neo4j.unsafe.batchinsert.BatchInserterIndex;
import org.neo4j.unsafe.batchinsert.BatchInserterIndexProvider;

/**
 * @author stefanp
 * @since 09.08.12
 */
@SuppressWarnings("UnusedDeclaration")
public class SheldonEdgeIndexer extends SheldonIndexer<EdgeEvent> implements EdgeIndexer {

    public void setup(final Config config) {
        super.setup(config);
        this.swapper = new IndexSwapper(config) {
            public BatchInserterIndex makeNew() {
                final BatchInserterIndexProvider indexProvider = config.getTargetIndexProvider();
                final BatchInserterIndex index =
                        indexProvider.relationshipIndex("sheldon_connection", MapUtil.stringMap("type", "exact"));
                assert index != null;
                return index;
            }
        };
    }

    public final Kind getKind() {
        return Kind.EDGE;
    }

}
