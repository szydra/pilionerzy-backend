package pl.pilionerzy.util;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;

public class LevelUtilsTest {

    @Test
    public void testNegativeLevel() {
        assertThatIllegalArgumentException()
                .isThrownBy(() -> LevelUtils.getGuaranteedLevel(-5));
    }

    @Test
    public void testTooLargeLevel() {
        assertThatIllegalArgumentException()
                .isThrownBy(() -> LevelUtils.getGuaranteedLevel(13));
    }

    @Test
    public void testLevel10() {
        int guaranteedLevel = LevelUtils.getGuaranteedLevel(10);
        assertThat(guaranteedLevel).isEqualTo(7);
    }

    @Test
    public void testLevel7() {
        int guaranteedLevel = LevelUtils.getGuaranteedLevel(7);
        assertThat(guaranteedLevel).isEqualTo(7);
    }

    @Test
    public void testLevel1() {
        int guaranteedLevel = LevelUtils.getGuaranteedLevel(1);
        assertThat(guaranteedLevel).isZero();
    }

    @Test
    public void testNextLevel() {
        int nextLevel = LevelUtils.getNextLevel(3);
        assertThat(nextLevel).isEqualTo(4);
    }

    @Test
    public void testHighestLevel() {
        assertThat(LevelUtils.isHighestLevel(12)).isTrue();
    }

    @Test
    public void testNonHighestLevel() {
        assertThat(LevelUtils.isHighestLevel(11)).isFalse();
    }

}
