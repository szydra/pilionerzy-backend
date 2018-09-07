package pl.pilionerzy.util;

import org.junit.Test;

import static org.junit.Assert.*;

public class LevelUtilsTest {

    @Test(expected = IllegalArgumentException.class)
    public void testNegativeLevel() {
        LevelUtils.getGuaranteedLevel(-5);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testTooLargeLevel() {
        LevelUtils.getGuaranteedLevel(13);
    }

    @Test
    public void testLevel10() {
        assertEquals(7, LevelUtils.getGuaranteedLevel(10));
    }

    @Test
    public void testLevel7() {
        assertEquals(7, LevelUtils.getGuaranteedLevel(7));
    }

    @Test
    public void testLevel1() {
        assertEquals(0, LevelUtils.getGuaranteedLevel(1));
    }

    @Test
    public void testNextLevel() {
        assertEquals(4, LevelUtils.getNextLevel(3));
    }

    @Test
    public void testHighestLevel() {
        assertTrue(LevelUtils.isHighestLevel(12));
    }

    @Test
    public void testNonHighestLevel() {
        assertFalse(LevelUtils.isHighestLevel(11));
    }

}