package com.moviepilot.sheldon.compactor.handler;

import com.moviepilot.sheldon.compactor.event.EdgeEvent;

/**
 * SheldonEdgeIndexer
 *
 * @author stefanp
 * @since 06.08.2012
 */
public interface EdgeIndexer extends Indexer<EdgeEvent>, EdgeEventHandler {
}
