package com.moviepilot.sheldon.compactor.handler;

import com.moviepilot.sheldon.compactor.event.NodeEvent;

/**
 * SheldonNodeIndexer
 *
 * @author stefanp
 * @since 06.08.2012
 */
public interface NodeIndexer extends Indexer<NodeEvent>, NodeEventHandler {
}
