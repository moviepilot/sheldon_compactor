package com.moviepilot.sheldon.compactor.custom;

import static junit.framework.Assert.*;

import gnu.trove.set.TLongSet;
import gnu.trove.set.hash.TLongHashSet;
import org.junit.Test;

/**
 * @author stefanp
 * @since 15.08.12
 */
public class ToolboxTest {

    @Test
    public void testMaxIntConversion() {
        assertEquals(Toolbox.long2int(Integer.MAX_VALUE), (long) Integer.MAX_VALUE);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testLowerIntConversionBounds() {
        Toolbox.long2int(((long)Integer.MIN_VALUE)-1L);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testUpperIntConversionBounds() {
        Toolbox.long2int(((long)Integer.MAX_VALUE)+1L);
    }

    @Test
    public void testIntPacking() {
        final long value = Toolbox.intint2long(1, 2);
        assertEquals(Toolbox.long2int1(value), 1);
        assertEquals(Toolbox.long2int0(value), 2);
    }


    @Test
    public void testMinIntPacking() {
        final long value = Toolbox.intint2long(Integer.MIN_VALUE, Integer.MIN_VALUE);
        assertEquals(Toolbox.long2int1(value), Integer.MIN_VALUE);
        assertEquals(Toolbox.long2int0(value), Integer.MIN_VALUE);
    }


    @Test
    public void testMaxIntPacking() {
        final long value = Toolbox.intint2long(Integer.MAX_VALUE, Integer.MAX_VALUE);
        assertEquals(Toolbox.long2int1(value), Integer.MAX_VALUE);
        assertEquals(Toolbox.long2int0(value), Integer.MAX_VALUE);
    }


    @Test
    public void testMixedIntPacking1() {
        final long value = Toolbox.intint2long(Integer.MIN_VALUE, Integer.MAX_VALUE);
        assertEquals(Toolbox.long2int1(value), Integer.MIN_VALUE);
        assertEquals(Toolbox.long2int0(value), Integer.MAX_VALUE);
    }

    @Test
    public void testMixedIntPacking2() {
        final long value = Toolbox.intint2long(Integer.MAX_VALUE, Integer.MIN_VALUE);
        assertEquals(Toolbox.long2int1(value), Integer.MAX_VALUE);
        assertEquals(Toolbox.long2int0(value), Integer.MIN_VALUE);
    }

    @Test
    public void testLongCompression() {
        final long value0 = Toolbox.compress2longs(Integer.MAX_VALUE, Integer.MIN_VALUE);
        final long value1 = Toolbox.compress2longs(Integer.MIN_VALUE, Integer.MAX_VALUE);
        assertNotSame(value0, value1);
    }

    @Test
    public void testIsNewPair() {
        final long value0 = Toolbox.compress2longs(Integer.MAX_VALUE, Integer.MIN_VALUE);
        final long value1 = Toolbox.compress2longs(Integer.MIN_VALUE, Integer.MAX_VALUE);
        final TLongSet set = new TLongHashSet();
        assertFalse(set.contains(value0));
        assertFalse(set.contains(value1));
        set.add(value0);
        assertTrue(set.contains(value0));
        assertFalse(set.contains(value1));
        set.add(value1);
        assertTrue(set.contains(value0));
        assertTrue(set.contains(value1));
        set.clear();
        assertFalse(set.contains(value0));
        assertFalse(set.contains(value1));
    }
}
