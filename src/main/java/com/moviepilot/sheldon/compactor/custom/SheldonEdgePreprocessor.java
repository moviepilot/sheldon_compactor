package com.moviepilot.sheldon.compactor.custom;

import com.moviepilot.sheldon.compactor.ModMapModifier;
import com.moviepilot.sheldon.compactor.event.EdgeEvent;
import com.moviepilot.sheldon.compactor.event.PropertyContainerEvent;
import com.moviepilot.sheldon.compactor.handler.EdgeEventHandler;
import gnu.trove.map.TObjectLongMap;
import gnu.trove.set.TLongSet;
import gnu.trove.set.hash.TLongHashSet;

/**
 * @author stefanp
 * @since 08.08.12
 */
@SuppressWarnings("UnusedDeclaration")
public class SheldonEdgePreprocessor
        extends SheldonPreprocessor<EdgeEvent>
        implements EdgeEventHandler, ModMapModifier {

    public static final String ALL_STORIES_SUBSCRIPTIONS_KEY = "all_stories_subscriptions";
    public static final String FEATURED_STORIES_SUBSCRIPTIONS_KEY = "featured_stories_subscriptions";

    // public static final String ALL_STORIES_RELATED_TOS_KEY = "all_stories_related_tos";
    // public static final String FEATURED_STORIES_RELATED_TOS_KEY = "featured_stories_related_tos";

    private TLongSet subscriptionPairs = new TLongHashSet();
    // private TLongSet relatedPairs      = new TLongHashSet();

    @SuppressWarnings("UnnecessaryReturnStatement")
    @Override
    protected void onOkEvent(EdgeEvent event, long sequence, boolean endOfBatch) {
        if (mergeEdge(event, FEATURED_STORIES_SUBSCRIPTIONS_KEY, ALL_STORIES_SUBSCRIPTIONS_KEY, subscriptionPairs))
            return;

        // if (mergeEdge(event, typeName,
        //         FEATURED_STORIES_RELATED_TOS_KEY, ALL_STORIES_RELATED_TOS_TYPE, relatedPairs))
        //    return;
    }

    private boolean mergeEdge(final EdgeEvent event,
                              final String fromTypeName, final String toTypeName, final TLongSet seenPairs) {

        final String typeName = event.type.name();

        if (toTypeName.equals(typeName)) {
            if (! Toolbox.isNewPair(seenPairs, event.srcId, event.dstId)) {
                event.action = PropertyContainerEvent.Action.DELETE;
                getProgressor().tick("ignore_" + toTypeName);
            }
            return true;
        }
        if (fromTypeName.equals(typeName)) {
            if (   (event.action != PropertyContainerEvent.Action.DELETE)
                && Toolbox.isNewPair(seenPairs, event.srcId, event.dstId)) {
                event.type = EdgeEvent.TypeCache.getInstance().forName(toTypeName);
                getProgressor().tick("convert_" + fromTypeName);
            }
            else {
                event.action = PropertyContainerEvent.Action.DELETE;
                getProgressor().tick("ignore_" + fromTypeName);
            }
            return true;
        }
        return false;
    }

    public void modifyMap(final TObjectLongMap<String> modMap) {
        super.modifyMap(modMap);
        modMap.put("convert_" + FEATURED_STORIES_SUBSCRIPTIONS_KEY, 10000);
        modMap.put("ignore_" + FEATURED_STORIES_SUBSCRIPTIONS_KEY, 10000);
        modMap.put("ignore_" + ALL_STORIES_SUBSCRIPTIONS_KEY, 10000);

        // modMap.put("convert_" + FEATURED_STORIES_RELATED_TOS_KEY, 10000);
        // modMap.put("ignore_" + FEATURED_STORIES_RELATED_TOS_KEY, 10000);
        // modMap.put("ignore_" + ALL_STORIES_RELATED_TOS_KEY, 10000);
    }

    public Kind getKind() {
        return Kind.EDGE;
    }
}
