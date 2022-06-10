package pl.pilionerzy.service;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import pl.pilionerzy.model.Level;
import pl.pilionerzy.repository.LevelRepository;

import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.doReturn;

@RunWith(MockitoJUnitRunner.class)
public class LevelServiceTest {

    @Mock
    private LevelRepository levelRepository;

    @InjectMocks
    private LevelService levelService;

    @Before
    public void setLevels() {
        doReturn(mockLevels()).when(levelRepository).findByOrderByIdAsc();
    }

    @Test
    public void testNegativeLevel() {
        assertThatIllegalArgumentException()
                .isThrownBy(() -> levelService.getGuaranteedLevel(-5));
    }

    @Test
    public void testTooLargeLevel() {
        assertThatIllegalArgumentException()
                .isThrownBy(() -> levelService.getGuaranteedLevel(13));
    }

    @Test
    public void testLevel10() {
        int guaranteedLevel = levelService.getGuaranteedLevel(10);
        assertThat(guaranteedLevel).isEqualTo(7);
    }

    @Test
    public void testLevel7() {
        int guaranteedLevel = levelService.getGuaranteedLevel(7);
        assertThat(guaranteedLevel).isEqualTo(7);
    }

    @Test
    public void testLevel1() {
        int guaranteedLevel = levelService.getGuaranteedLevel(1);
        assertThat(guaranteedLevel).isZero();
    }

    @Test
    public void testNoGuaranteedLevel() {
        doReturn(List.of(level(0, false), level(1, false)))
                .when(levelRepository)
                .findByOrderByIdAsc();

        assertThatIllegalStateException().isThrownBy(() -> levelService.getGuaranteedLevel(1));
    }

    @Test
    public void testHighestLevel() {
        assertThat(levelService.isHighestLevel(12)).isTrue();
    }

    @Test
    public void testNonHighestLevel() {
        assertThat(levelService.isHighestLevel(11)).isFalse();
    }

    private List<Level> mockLevels() {
        return List.of(
                level(0, true),
                level(1, false),
                level(2, true),
                level(3, false),
                level(4, false),
                level(5, false),
                level(6, false),
                level(7, true),
                level(8, false),
                level(9, false),
                level(10, false),
                level(11, false),
                level(12, true)
        );
    }

    private Level level(Integer id, Boolean guaranteed) {
        var level = new Level();
        level.setId(id);
        level.setAward(id.toString());
        level.setGuaranteed(guaranteed);
        return level;
    }
}
