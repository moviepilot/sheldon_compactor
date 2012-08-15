package com.moviepilot.sheldon.compactor.util;

/**
 * @author stefanp
 * @since 08.08.12
 */
public interface ProgressorHolder {
    public Progressor getProgressor();
    public void setProgressor(final Progressor aProgressor);
}
