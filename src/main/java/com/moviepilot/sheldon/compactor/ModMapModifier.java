package com.moviepilot.sheldon.compactor;

import gnu.trove.map.TObjectLongMap;

/**
 * @author stefanp
 * @since 08.08.12
 */
public interface ModMapModifier {
    public void modifyMap(TObjectLongMap<String> modMap);
}
