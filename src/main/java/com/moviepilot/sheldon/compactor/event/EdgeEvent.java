package com.moviepilot.sheldon.compactor.event;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.lmax.disruptor.EventFactory;
import com.moviepilot.sheldon.compactor.config.Config;
import com.moviepilot.sheldon.compactor.util.Progressor;
import org.neo4j.graphdb.DynamicRelationshipType;
import org.neo4j.graphdb.RelationshipType;

import java.util.concurrent.ExecutionException;

/**
 * EdgeEvent
 *
 * @author stefanp
 * @since 03.08.12
 */
public final class EdgeEvent extends PropertyContainerEvent {

    public long srcId;
    public long dstId;
    public RelationshipType type;


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

    public final static class TypeCache {
        private final static TypeCache instance = new TypeCache();

        private LoadingCache<String, RelationshipType> cache;

        private TypeCache() {
            cache = CacheBuilder.newBuilder().build(new CacheLoader<String, RelationshipType>() {
                public RelationshipType load(final String s) throws Exception {
                    if (s == null || s.length() == 0)
                        throw new IllegalArgumentException();
                    return DynamicRelationshipType.withName(s);
                }
            });
        }

        public static TypeCache getInstance() {
            return instance;
        }

        public RelationshipType forName(final String name) {
            try {
                return cache.get(name);
            } catch (ExecutionException e) {
                throw new RuntimeException(e.getCause());
            }
        }
    }
}


