package com.moviepilot.sheldon.compactor.custom;

import com.moviepilot.sheldon.compactor.ModMapModifier;
import com.moviepilot.sheldon.compactor.event.EdgeEvent;
import com.moviepilot.sheldon.compactor.handler.EdgeEventHandler;
import gnu.trove.map.TObjectLongMap;
import org.neo4j.graphdb.DynamicRelationshipType;

/**
 * @author stefanp
 * @since 08.08.12
 */
@SuppressWarnings("UnusedDeclaration")
public class SheldonEdgePreprocessor
        extends SheldonPreprocessor<EdgeEvent>
        implements EdgeEventHandler, ModMapModifier {

    public static final DynamicRelationshipType ALL_STORIES_SUBSCRIPTIONS_TYPE
            = DynamicRelationshipType.withName("all_stories_subscriptions");

    public static final DynamicRelationshipType ALL_STORIES_RELATED_TOS_TYPE
            = DynamicRelationshipType.withName("all_stories_related_tos");

    public static final String FEATURED_STORIES_SUBSCRIPTIONS_KEY = "featured_stories_subscriptions";

    public static final String FEATURED_STORIES_RELATED_TOS_KEY = "featured_stories_related_tos";

    @Override
    protected void onOkEvent(EdgeEvent event, long sequence, boolean endOfBatch) {
        final String typeName = event.type.name();
        if (FEATURED_STORIES_SUBSCRIPTIONS_KEY.equals(typeName)) {
            event.type = ALL_STORIES_SUBSCRIPTIONS_TYPE;
            getProgressor().tick(FEATURED_STORIES_SUBSCRIPTIONS_KEY);
        }
        if (FEATURED_STORIES_RELATED_TOS_KEY.equals(typeName)) {
            event.type = ALL_STORIES_RELATED_TOS_TYPE;
            getProgressor().tick(FEATURED_STORIES_RELATED_TOS_KEY);
        }
    }

    public void modifyMap(TObjectLongMap<String> modMap) {
        modMap.put(FEATURED_STORIES_SUBSCRIPTIONS_KEY, 1000);
        modMap.put(FEATURED_STORIES_RELATED_TOS_KEY, 1000);
    }
}
