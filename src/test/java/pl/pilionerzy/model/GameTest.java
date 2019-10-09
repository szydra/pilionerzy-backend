package pl.pilionerzy.model;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThatIllegalStateException;
import static pl.pilionerzy.assertion.Assertions.assertThat;

public class GameTest {

    private Game game = new Game();

    @Test
    public void shouldActivateInactiveGame() {
        game.activate();

        assertThat(game).isActive();
    }

    @Test
    public void shouldThrowExceptionWhenActivatingActiveGame() {
        game.setActive(true);

        assertThatIllegalStateException()
                .isThrownBy(() -> game.activate())
                .withMessage("Active game cannot be activated");
    }

    @Test
    public void shouldDeactivateActiveGame() {
        game.setActive(true);

        game.deactivate();

        assertThat(game).isInactive();
    }

    @Test
    public void shouldThrowExceptionWhenDeactivatingInactiveGame() {
        game.setActive(false);

        assertThatIllegalStateException()
                .isThrownBy(() -> game.deactivate())
                .withMessage("Inactive game cannot be deactivated");
    }

    @Test
    public void shouldInitLevelInANewGame() {
        game.initLevel();

        assertThat(game).hasLevel(0);
    }

    @Test
    public void shouldThrowExceptionWhenLevelIsInitialized() {
        game.setLevel(1);

        assertThatIllegalStateException()
                .isThrownBy(() -> game.initLevel())
                .withMessage("Level is already set");
    }

    @Test
    public void gamesWithTheSameBusinessIdShouldBeEqual() {
        game.setId(1L);
        game.setBusinessId("a1b2c3");
        game.setLevel(1);

        Game otherGame = new Game();
        otherGame.setId(2L);
        otherGame.setBusinessId("a1b2c3");
        otherGame.setLevel(2);

        assertThat(game).isEqualTo(otherGame);
    }

    @Test
    public void gamesWithDistinctBusinessIdsShouldNotBeEqual() {
        game.setBusinessId("a1b2c3");

        Game otherGame = new Game();
        otherGame.setBusinessId("b2c3a1");

        assertThat(game).isNotEqualTo(otherGame);
    }
}
