package pl.pilionerzy.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import pl.pilionerzy.model.Level;
import pl.pilionerzy.repository.LevelRepository;

import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.doReturn;

@ExtendWith(MockitoExtension.class)
class LevelServiceTest {

    @Mock
    private LevelRepository levelRepository;

    @InjectMocks
    private LevelService levelService;

    @ParameterizedTest
    @ValueSource(ints = {-5, 13})
    void testLevelsOutOfRange(int level) {
        // given: mocked levels
        doReturn(mockLevels()).when(levelRepository).findByOrderByIdAsc();

        // when: trying to get guaranteed level for level out of range
        // then: exception is thrown
        assertThatIllegalArgumentException()
                .isThrownBy(() -> levelService.getGuaranteedLevel(level));
    }

    @ParameterizedTest
    @CsvSource({"1,0", "7,7", "10,7"})
    void testGuaranteedLevels(int level, int expectedGuaranteedLevel) {
        // given
        doReturn(mockLevels()).when(levelRepository).findByOrderByIdAsc();

        // when
        int guaranteedLevel = levelService.getGuaranteedLevel(level);

        // then
        assertThat(guaranteedLevel).isEqualTo(expectedGuaranteedLevel);
    }

    @Test
    void testNoGuaranteedLevel() {
        // given: only non-guaranteed levels
        doReturn(List.of(level(0, false), level(1, false)))
                .when(levelRepository)
                .findByOrderByIdAsc();

        // when: trying to get guaranteed level
        // then: exception is thrown
        assertThatIllegalStateException().isThrownBy(() -> levelService.getGuaranteedLevel(1));
    }

    @ParameterizedTest
    @CsvSource({"11,false", "12,true"})
    void testHighestLevel(int level, boolean highest) {
        // given
        doReturn(mockLevels()).when(levelRepository).findByOrderByIdAsc();

        // when
        var isHighestLevel = levelService.isHighestLevel(level);

        // then
        assertThat(isHighestLevel).isEqualTo(highest);
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
