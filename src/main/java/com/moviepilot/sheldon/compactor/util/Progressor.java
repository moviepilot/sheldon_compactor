package com.moviepilot.sheldon.compactor.util;

import com.moviepilot.sheldon.compactor.config.Defaults;
import gnu.trove.map.TObjectLongMap;
import gnu.trove.map.hash.TObjectLongHashMap;

/**
 * Utility class for progress counters
 *
 *
 * @author stefanp
 * @since 03.08.12
 */
public class Progressor {

    public static TObjectLongMap<String> makeCountMap() {
        return new TObjectLongHashMap<String>(Defaults.DEFAULT_NUM_COUNTS);
    }

    public static TObjectLongMap<String> makeTimeMap() {
        return new TObjectLongHashMap<String>(Defaults.DEFAULT_NUM_COUNTS);
    }

    private final TObjectLongMap<String> counts = makeCountMap();
    private final TObjectLongMap<String> times = makeTimeMap();
    private final TObjectLongMap<String> mods;

    private final long created = System.currentTimeMillis();

    public Progressor(TObjectLongMap<String> mods) {
        this.mods = mods;
        for (String name : mods.keySet())
            times.put(name, created);
    }

    public void tick(String name) {
        final long count = counts.adjustOrPutValue(name, 1L, 1L);

        if (!mods.containsKey(name))
            print(name, count);
        else {
            if ((count % mods.get(name)) == 0) {
                final long now  = System.currentTimeMillis();
                final long last = times.get(name);
                print(name, count, last-now);
                times.put(name, now);
            }
        }
    }

    public void printAll() {
        for (String key : counts.keySet())
            print(key);
    }

    public void print(String name) {
        print(name, counts.get(name));
    }

    private void print(String name, long count) {
        print(name, count, (System.currentTimeMillis() - created));
    }

    private void print(String name, long count, long time) {
        if (time < 0)
            System.out.println(
                    (System.currentTimeMillis() - created) + " (since start) "
                    + (-time) + " (since last) " + name + ": " + count);
        else
            System.out.println(time + " (since start) " + name + ": " + count);
    }
}