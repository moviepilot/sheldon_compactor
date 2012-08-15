package com.moviepilot.sheldon.compactor.producer.glops;

import com.lmax.disruptor.dsl.Disruptor;
import com.moviepilot.sheldon.compactor.config.Config;
import com.moviepilot.sheldon.compactor.event.EdgeEvent;
import com.moviepilot.sheldon.compactor.event.EdgeEventTranslator;
import com.moviepilot.sheldon.compactor.producer.PropertyContainerEventProducer;
import com.moviepilot.sheldon.compactor.util.Progressor;
import org.neo4j.graphdb.Relationship;
import org.neo4j.kernel.EmbeddedGraphDatabase;
import org.neo4j.tooling.GlobalGraphOperations;

/**
 * @author stefanp
 * @since 08.08.12
 */
public class GlopsEdgeEventProducer extends PropertyContainerEventProducer<EdgeEvent> {

    protected final Config config;
    protected final EmbeddedGraphDatabase src;

    public GlopsEdgeEventProducer(final Config config, final EmbeddedGraphDatabase src) {
        this.config = config;
        this.src    = src;
    }

    public void produce(final Disruptor<EdgeEvent> disruptor, final Progressor progressor) {
        // event translator
        final EdgeEventTranslator trans = new EdgeEventTranslator(config, progressor);

        // produce edge events
        for (final Relationship rel : GlobalGraphOperations.at(src).getAllRelationships()) {
            try {
                trans.setContainer(rel);
                disruptor.publishEvent(trans);
                progressor.tick("edge_read");
            }
            catch (RuntimeException e) {
                progressor.tick("edge_read_error");
            }
        }
    }
}
