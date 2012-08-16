package com.moviepilot.sheldon.compactor.producer.glops;

import com.lmax.disruptor.dsl.Disruptor;
import com.moviepilot.sheldon.compactor.config.Config;
import com.moviepilot.sheldon.compactor.event.NodeEvent;
import com.moviepilot.sheldon.compactor.event.NodeEventTranslator;
import com.moviepilot.sheldon.compactor.producer.PropertyContainerEventProducer;
import com.moviepilot.sheldon.compactor.util.Progressor;
import org.neo4j.graphdb.Node;
import org.neo4j.kernel.EmbeddedGraphDatabase;
import org.neo4j.tooling.GlobalGraphOperations;

/**
 * @author stefanp
 * @since 08.08.12
 */
public class GlopsNodeEventProducer extends PropertyContainerEventProducer<NodeEvent> {

    protected final Config config;
    protected final EmbeddedGraphDatabase src;

    public GlopsNodeEventProducer(final Config config, final EmbeddedGraphDatabase src) {
        this.config = config;
        this.src    = src;
    }

    public void produce(final Disruptor<NodeEvent> disruptor, final Progressor progressor) {
        // event translator
        final NodeEventTranslator trans = new NodeEventTranslator(config, progressor, src.getReferenceNode().getId());

        // produce node events
        for (Node node : GlobalGraphOperations.at(src).getAllNodes()) {
            try {
                trans.setContainer(node);
                disruptor.publishEvent(trans);
                progressor.tick("node_read");
            } catch (RuntimeException e) {
                progressor.tick("node_read_error");
            }
        }
    }
}
