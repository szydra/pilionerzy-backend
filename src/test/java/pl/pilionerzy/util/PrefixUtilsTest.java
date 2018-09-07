package pl.pilionerzy.util;

import org.junit.Test;
import pl.pilionerzy.exception.IllegalPrefixException;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class PrefixUtilsTest {

    @Test(expected = IllegalPrefixException.class)
    public void testNull() {
        PrefixUtils.validatePrefix(null);
    }

    @Test(expected = IllegalPrefixException.class)
    public void testSpace() {
        PrefixUtils.validatePrefix(" ");
    }

    @Test(expected = IllegalPrefixException.class)
    public void testInvalidPrefix() {
        PrefixUtils.validatePrefix(" Q ");
    }

    @Test
    public void testValidPrefix() {
        PrefixUtils.validatePrefix("  B  ");
    }

    @Test
    public void testA() {
        assertTrue(PrefixUtils.isValid("A"));
    }

    @Test
    public void testZ() {
        assertFalse(PrefixUtils.isValid("Z"));
    }

    @Test
    public void testAa() {
        assertFalse(PrefixUtils.isValid("AA"));
    }

}