package com.moviepilot.sheldon.compactor.event;

import com.moviepilot.sheldon.compactor.config.Config;
import com.moviepilot.sheldon.compactor.config.Defaults;
import com.moviepilot.sheldon.compactor.util.Progressor;
import gnu.trove.map.TObjectIntMap;
import org.neo4j.graphdb.Node;

/**
 * NodeEventTranslator
 *
 *
 * @author stefanp
 * @since 03.08.12
 */
public final class NodeEventTranslator extends PropertyContainerEventTranslator<Node, NodeEvent> {

    public static final String ERROR_NAME = "node_trans_error";

    public NodeEventTranslator(final Config config, final Progressor aProgressor) {
        super(config, aProgressor, ERROR_NAME);
    }

    public void translateTo_(final NodeEvent event, long sequence) {
        final Node node = getContainer();
        event.id        = node.getId();
        event.action    = event.id == Defaults.REFERENCE_NODE_ID ?
                PropertyContainerEvent.Action.UPDATE : PropertyContainerEvent.Action.CREATE;
        putAllContainerProperties(node, event.props);
    }
}
