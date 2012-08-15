package com.moviepilot.sheldon.compactor;

import com.lmax.disruptor.EventFactory;
import com.lmax.disruptor.dsl.Disruptor;
import com.lmax.disruptor.dsl.EventHandlerGroup;
import com.moviepilot.sheldon.compactor.config.Config;
import com.moviepilot.sheldon.compactor.event.*;
import com.moviepilot.sheldon.compactor.handler.*;
import com.moviepilot.sheldon.compactor.producer.PropertyContainerEventProducer;
import com.moviepilot.sheldon.compactor.util.Progressor;
import com.moviepilot.sheldon.compactor.util.ProgressorHolder;
import gnu.trove.map.TObjectLongMap;
import org.neo4j.unsafe.batchinsert.BatchInserter;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Copy all nodes and relationships of a neo4j database into a new database
 *
 * You may want to understand how disruptor works before digging into this
 *
 * @author stefanp
 * @since 03.08.12
 */
public final class Compactor implements Runnable {

    // Data sources
    private final PropertyContainerEventProducer<NodeEvent> nodeProducer;

    private final PropertyContainerEventProducer<EdgeEvent> edgeProducer;

    // Target database
    private final BatchInserter targetDb;

    // Mod map for progressors
    private final TObjectLongMap<String> modMap;

    // Compactor config provided by builder
    private Config config;

    public Compactor(final Config config,
                     final PropertyContainerEventProducer<NodeEvent> nodeProducer,
                     final PropertyContainerEventProducer<EdgeEvent> edgeProducer) {

        // Copy arguments to final member variables
        this.nodeProducer = nodeProducer;
        this.edgeProducer = edgeProducer;
        this.config       = config;
        this.targetDb     = config.getTargetDatabase();
        this.modMap       = config.getModMap();
    }

    public void run() {
        copyNodes(config.getOptNodeHandler(), config.getOptNodeIndexer());
        copyEdges(config.getOptEdgeHandler(), config.getOptEdgeIndexer());
    }

    private void copyNodes(final NodeEventHandler optNodeHandler,
                             final NodeIndexer optNodeIndexer) {
        final Copier<NodeEvent, NodeEventHandler, NodeIndexer> copier =
                new Copier<NodeEvent, NodeEventHandler, NodeIndexer>(config.getNumExtraNodeThreads()) {

                    protected EventFactory<NodeEvent> makeEventFactory() {
                        return new NodeEvent.Factory(config);
                    }

                    protected PropertyContainerEventHandler<NodeEvent> makeWriter() {
                        return new NodeWriter();
                    }
                };
        copier.copy(nodeProducer, optNodeHandler, optNodeIndexer);
    }


    private void copyEdges(final EdgeEventHandler optEdgeHandler,
                             final EdgeIndexer optEdgeIndexer) {
        final Copier<EdgeEvent, EdgeEventHandler, EdgeIndexer> copier =
                new Copier<EdgeEvent, EdgeEventHandler, EdgeIndexer>(config.getNumExtraEdgeThreads()) {

                    protected EventFactory<EdgeEvent> makeEventFactory() {
                        return new EdgeEvent.Factory(config);
                    }

                    protected PropertyContainerEventHandler<EdgeEvent> makeWriter() {
                        return new EdgeWriter();
                    }
                };
        copier.copy(edgeProducer, optEdgeHandler, optEdgeIndexer);
    }

    private abstract class Copier<E extends PropertyContainerEvent,
            H extends PropertyContainerEventHandler<E>,
            I extends PropertyContainerEventHandler<E> & Indexer<E>> {

        private final ExecutorService executorService;

        Copier(final int numExtraThreads) {
            this.executorService = Executors.newFixedThreadPool(3 + numExtraThreads);
        }

        void copy(final PropertyContainerEventProducer<E> producer, final H optHandler, final I optIndexer) {
            // allow optional handlers to customize the mod map
            setupModMap(optHandler);
            setupModMap(optIndexer);

            // create progressors for various handlers
            final Progressor copyingProgressor  = new Progressor(modMap);
            final Progressor handlerProgressor  = setupOptProgressor(optHandler);
            final Progressor indexingProgressor = setupOptProgressor(optIndexer);

            // setup disruptor
            final PropertyCleaner<E> propertyCleaner = new PropertyCleaner<E>(modMap);
            final Disruptor<E> disruptor             = newDisruptor(optHandler, optIndexer, propertyCleaner);

            // run disruptor
            producer.run(disruptor, executorService, copyingProgressor);

            if (optIndexer != null)
                optIndexer.flush();

            // print progress count summary
            copyingProgressor.printAll();
            if (handlerProgressor != null)
                handlerProgressor.printAll();
            if (indexingProgressor != null)
                indexingProgressor.printAll();
            propertyCleaner.getProgressor().printAll();
        }


        @SuppressWarnings({"unchecked"})
        private Disruptor<E> newDisruptor(
                final H optHandler,
                final I optIndexer,
                final PropertyCleaner propertyCleaner) {

            final Disruptor<E> disruptor = new Disruptor<E>(makeEventFactory(), config.getRingSize(), executorService);

            EventHandlerGroup<E> handlerGroup;
            if (optHandler == null) {
                handlerGroup = disruptor.handleEventsWith(makeWriter());
            }
            else {
                handlerGroup = disruptor.handleEventsWith(optHandler);
                handlerGroup = handlerGroup.then(makeWriter());
            }

            if (optIndexer != null) {
                optIndexer.setup(config);
                handlerGroup = handlerGroup.then(optIndexer).then(new IndexWriter<E>(config));
            }

            handlerGroup.then(propertyCleaner);

            return disruptor;

        }

        private void setupModMap(PropertyContainerEventHandler<E> optHandler) {
            if (optHandler != null && (optHandler instanceof ModMapModifier)) {
                ((ModMapModifier)optHandler).modifyMap(modMap);
            }
        }

        private Progressor setupOptProgressor(final PropertyContainerEventHandler<E> optHandler)  {
            if (optHandler != null && (optHandler instanceof ProgressorHolder)) {
                final Progressor optProgressor = new Progressor(modMap);
                ((ProgressorHolder)optHandler).setProgressor(optProgressor);
                return optProgressor;
            }
            else
                return null;
        }

        protected abstract EventFactory<E> makeEventFactory();

        protected abstract PropertyContainerEventHandler<E> makeWriter();
    }

    /**
     * Handler for writing nodes into the new database
     */
    private class NodeWriter
            extends AbstractPropertyContainerEventHandler<NodeEvent>
            implements NodeEventHandler {

        public NodeWriter() {
            super(Compactor.this.modMap);
        }

        @Override
        public void onEvent(NodeEvent event, long sequence, boolean endOfBatch) throws Exception {
            if (event.isOk()) {
                try {
                    switch (event.action) {
                        case CREATE:
                            targetDb.createNode(event.id, event.getProps());
                            getProgressor().tick("node_create");
                            break;
                        case UPDATE:
                            targetDb.setNodeProperties(event.id, event.getProps());
                            getProgressor().tick("node_update");
                            break;
                        case DELETE:
                            getProgressor().tick("node_delete");
                            // nothing
                        default:
                            throw new IllegalStateException("Unknown update mode");
                    }
                    getProgressor().tick("node_write");
                }
                catch (RuntimeException e) {
                    event.failure = e;
                    getProgressor().tick("node_write_error");
                }
            }
        }
    }

    /**
     * Handler for writing edges into the new database
     */
    private class EdgeWriter
            extends AbstractPropertyContainerEventHandler<EdgeEvent>
            implements EdgeEventHandler  {

        public EdgeWriter() {
            super(Compactor.this.modMap);
        }

        @Override
        public void onEvent(EdgeEvent event, long sequence, boolean endOfBatch) throws Exception {
            if (event.isOk()) {
                try {
                    switch (event.action) {
                        case CREATE:
                            event.id = targetDb.createRelationship(event.srcId,  event.dstId, event.type,
                                    event.getProps());
                            getProgressor().tick("edge_create");
                            break;
                        case UPDATE:
                            targetDb.setRelationshipProperties(event.id, event.getProps());
                            getProgressor().tick("edge_update");
                            break;
                        case DELETE:
                            getProgressor().tick("edge_delete");
                            // nothing
                        default:
                            throw new IllegalStateException("Unknown update mode");
                    }
                    getProgressor().tick("edge_write");
                }
                catch (RuntimeException e) {
                    event.failure = e;
                    getProgressor().tick("edge_write_error");
                }
            }
        }
    }
}
