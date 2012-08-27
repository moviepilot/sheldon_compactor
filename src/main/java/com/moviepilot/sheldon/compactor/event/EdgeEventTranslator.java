package com.moviepilot.sheldon.compactor.event;

import com.moviepilot.sheldon.compactor.config.Config;
import com.moviepilot.sheldon.compactor.util.Progressor;
import gnu.trove.map.TObjectIntMap;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;

/**
 * EdgeEventTranslator
 *
 * @author stefanp
 * @since 03.08.12
 */
public class EdgeEventTranslator extends PropertyContainerEventTranslator<Relationship, EdgeEvent> {

    public static final String ERROR_NAME = "edge_trans_error";

    public EdgeEventTranslator(final Config config, final Progressor progressor) {
        super(config, progressor, ERROR_NAME);
    }

    public void translateTo_(final EdgeEvent event, long sequence) {
        final Relationship rel = getContainer();
        event.action = PropertyContainerEvent.Action.CREATE;
        event.id     = rel.getId();
        event.srcId  = rel.getStartNode().getId();
        event.dstId  = rel.getEndNode().getId();
        event.type   = EdgeEvent.TypeCache.getInstance().forName(rel.getType().name());
        putAllContainerProperties(rel, event.props);
    }
}
