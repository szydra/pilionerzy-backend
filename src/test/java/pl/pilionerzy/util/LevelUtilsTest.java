package pl.pilionerzy.util;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;
import static pl.pilionerzy.util.LevelUtils.getGuaranteedLevel;
import static pl.pilionerzy.util.LevelUtils.isHighestLevel;

public class LevelUtilsTest {

    @Test
    public void testNegativeLevel() {
        assertThatIllegalArgumentException()
                .isThrownBy(() -> getGuaranteedLevel(-5));
    }

    @Test
    public void testTooLargeLevel() {
        assertThatIllegalArgumentException()
                .isThrownBy(() -> getGuaranteedLevel(13));
    }

    @Test
    public void testLevel10() {
        int guaranteedLevel = getGuaranteedLevel(10);
        assertThat(guaranteedLevel).isEqualTo(7);
    }

    @Test
    public void testLevel7() {
        int guaranteedLevel = getGuaranteedLevel(7);
        assertThat(guaranteedLevel).isEqualTo(7);
    }

    @Test
    public void testLevel1() {
        int guaranteedLevel = getGuaranteedLevel(1);
        assertThat(guaranteedLevel).isZero();
    }

    @Test
    public void testNextLevel() {
        int nextLevel = LevelUtils.getNextLevel(3);
        assertThat(nextLevel).isEqualTo(4);
    }

    @Test
    public void testHighestLevel() {
        assertThat(isHighestLevel(12)).isTrue();
    }

    @Test
    public void testNonHighestLevel() {
        assertThat(isHighestLevel(11)).isFalse();
    }
}
