package com.moviepilot.sheldon.compactor.producer;

import com.lmax.disruptor.dsl.Disruptor;
import com.moviepilot.sheldon.compactor.event.PropertyContainerEvent;
import com.moviepilot.sheldon.compactor.util.Progressor;

import java.util.concurrent.ExecutorService;

/**
 * @author stefanp
 * @since 08.08.12
 */
public abstract class PropertyContainerEventProducer<E extends PropertyContainerEvent> {
    public abstract void produce(final Disruptor<E> disruptor, final Progressor progressor);

    public void run(final Disruptor<E> disruptor,
                    final ExecutorService executorService,
                    final Progressor progressor) {
        disruptor.start();

        produce(disruptor, progressor);

        disruptor.shutdown();
        if (executorService != null)
            executorService.shutdown();
    }
}
