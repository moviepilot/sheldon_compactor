package com.moviepilot.sheldon.compactor.custom;

import gnu.trove.set.TLongSet;

/**
 * @author stefanp
 * @since 15.08.12
 */
public class Toolbox {

    public static boolean isNewPair(final TLongSet pairSet, long srcId, long dstId) {
        final long key = compress2longs(srcId, dstId);
        if (pairSet.contains(key))
            return false;
        else  {
            pairSet.add(key);
            return true;
        }
    }

    public static long compress2longs(final long a, final long b) {
        return  intint2long(long2int(a), long2int(b));
    }

    public static long intint2long(final int a, final int b) {
        final long long_a = ((long)a) << Integer.SIZE;
        final long long_b = ((long)b);
        return long_a & long_b;
    }

    public static int long2int(final long value) {
        if (value < Integer.MIN_VALUE)
            throw new IllegalArgumentException("Can't convert to int: too small");
        if (value > Integer.MAX_VALUE)
            throw new IllegalArgumentException("Can't convert to int: too big");
        return (int) value;
    }
}
