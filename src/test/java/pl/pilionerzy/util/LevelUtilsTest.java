package pl.pilionerzy.util;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static pl.pilionerzy.util.LevelUtils.isHighestLevel;

public class LevelUtilsTest {

    @Test
    public void testHighestLevel() {
        assertThat(isHighestLevel(12)).isTrue();
    }

    @Test
    public void testNonHighestLevel() {
        assertThat(isHighestLevel(11)).isFalse();
    }
}
