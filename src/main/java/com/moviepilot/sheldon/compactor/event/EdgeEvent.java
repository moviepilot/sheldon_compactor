package com.moviepilot.sheldon.compactor.event;

import com.lmax.disruptor.EventFactory;
import com.moviepilot.sheldon.compactor.config.Config;
import com.moviepilot.sheldon.compactor.util.Progressor;
import org.neo4j.graphdb.RelationshipType;

/**
 * EdgeEvent
 *
 * @author stefanp
 * @since 03.08.12
 */
public final class EdgeEvent extends PropertyContainerEvent {

    public long srcId;
    public long dstId;
    public String type;


    public EdgeEvent(final Config config) {
        super(config);
    }

    @Override
    protected void reset() {
        super.reset();
        srcId = -1L;
        dstId = -1L;
        type  = null;
    }


    @Override
    public String toString() {
        return getClass().getName() +
               "{" +
                "id=" + id +
                ", srcId=" + srcId +
                ", dstId=" + dstId +
                ", type=" + type +
                ", action=" + action +
                ", props=" + props +
                ", failure=" + failure +
                '}';
    }

    final public static class Factory extends PropertyContainerEventFactory<EdgeEvent> {

        public Factory(final Config config) {
            super(config);
        }

        public EdgeEvent newInstance() {
            return new EdgeEvent(config);
        }
    }
}


