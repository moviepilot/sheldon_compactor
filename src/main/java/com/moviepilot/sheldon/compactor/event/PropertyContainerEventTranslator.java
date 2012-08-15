package com.moviepilot.sheldon.compactor.event;

import com.lmax.disruptor.EventTranslator;
import com.moviepilot.sheldon.compactor.config.Config;
import com.moviepilot.sheldon.compactor.util.Progressor;
import gnu.trove.map.TMap;
import org.neo4j.graphdb.PropertyContainer;

import java.util.Map;

/**
 * PropertyContainerEventTranslator
 *
 * Translates neo4j PropertyContainers into PropertyContainerEvents
 *
 * @author stefanp
 * @since 05.08.12
 */
public abstract class PropertyContainerEventTranslator<P extends PropertyContainer, E extends PropertyContainerEvent>
    implements EventTranslator<E> {

    public final Progressor progressor;
    public final String errorName;
    public final Config config;

    private P container;

    public PropertyContainerEventTranslator(final Config config, final Progressor progressor, final String errorName) {
        this.progressor = progressor;
        this.errorName  = errorName;
        this.config     = config;
    }

    public P getContainer() {
        return container;
    }

    public void setContainer(P container) {
        this.container = container;
    }

    public Map<String, Object> getContainerProperties() {
        final TMap<String, Object> propMap = PropertyContainerEvent.makeNewPropertyMap(config);
        putAllContainerProperties(propMap);
        return propMap;
    }

    public void putAllContainerProperties(final TMap<String, Object> result) {
        putAllContainerProperties(getContainer(), result);
    }

    @SuppressWarnings({"ConstantConditions"})
    protected void putAllContainerProperties(final P aContainer, final TMap<String, Object> result) {
        for (String property : aContainer.getPropertyKeys()) {
            try {
                result.put(property, aContainer.getProperty(property));
            }
            catch (Exception ex) {
                System.err.println("Broken property " + property + " in " + aContainer);
                progressor.tick("prop_error");
            }
        }
    }

    @Override
    public void translateTo(E event, long sequence) {
       assert event.isOk();

        try {
            translateTo_(event, sequence);
        } catch (RuntimeException e) {
            event.failure = e;
            progressor.tick(errorName);
        }
    }

    protected abstract void translateTo_(E event, long sequence);
}
