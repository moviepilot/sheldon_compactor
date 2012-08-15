package com.moviepilot.sheldon.compactor.util;

import gnu.trove.map.TObjectLongMap;

/**
 * ProgressReporter
 *
 * @author stefanp
 * @since 03.08.12
 */
public class ProgressReporter {
    private final Progressor progressor;

    public ProgressReporter(final TObjectLongMap<String> modMap) {
        progressor = new Progressor(modMap);
    }

    public Progressor getProgressor() {
        return progressor;
    }
}
