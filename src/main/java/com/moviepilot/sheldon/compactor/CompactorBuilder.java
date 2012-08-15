package com.moviepilot.sheldon.compactor;

import com.moviepilot.sheldon.compactor.config.Config;

/**
 * @author stefanp
 * @since 08.08.12
 */
public interface CompactorBuilder {
    public Compactor build(final Config config);
}
