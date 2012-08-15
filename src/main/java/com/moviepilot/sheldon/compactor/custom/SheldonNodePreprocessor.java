package com.moviepilot.sheldon.compactor.custom;

import com.moviepilot.sheldon.compactor.event.NodeEvent;
import com.moviepilot.sheldon.compactor.handler.NodeEventHandler;

/**
 * @author stefanp
 * @since 08.08.12
 */
@SuppressWarnings("UnusedDeclaration")
public class SheldonNodePreprocessor extends SheldonPreprocessor<NodeEvent> implements NodeEventHandler {

    protected void onOkEvent(NodeEvent event, long sequence, boolean endOfBatch) {
    }
}
