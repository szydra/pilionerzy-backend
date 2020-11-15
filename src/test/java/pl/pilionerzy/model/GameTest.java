package pl.pilionerzy.model;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThatIllegalStateException;
import static pl.pilionerzy.assertion.Assertions.assertThat;

public class GameTest {

    @Test
    public void shouldActivateInactiveGame() {
        // given: a new game
        Game game = new Game();

        // when: activating it
        game.activate();

        // then: the game is active
        assertThat(game).isActive();
    }

    @Test
    public void shouldThrowExceptionWhenActivatingActiveGame() {
        // given: an active game
        Game game = new Game();
        game.setActive(true);

        // when: trying to activate it
        // then: exception is thrown
        assertThatIllegalStateException()
                .isThrownBy(game::activate)
                .withMessage("Active game cannot be activated");
    }

    @Test
    public void shouldDeactivateActiveGame() {
        // given: an active game
        Game game = new Game();
        game.setActive(true);

        // when: deactivating it
        game.deactivate();

        // then: the game is inactive
        assertThat(game).isInactive();
    }

    @Test
    public void shouldThrowExceptionWhenDeactivatingInactiveGame() {
        // given: an inactive game
        Game game = new Game();
        game.setActive(false);

        // when: trying do deactivate it
        // then: exception is thrown
        assertThatIllegalStateException()
                .isThrownBy(game::deactivate)
                .withMessage("Inactive game cannot be deactivated");
    }

    @Test
    public void shouldInitializeLevelInANewGame() {
        // given: a new game
        Game game = new Game();

        // when: initializing level
        game.initLevel();

        // then: the game level is 0
        assertThat(game).hasLevel(0);
    }

    @Test
    public void shouldThrowExceptionWhenLevelWasInitialized() {
        // given: a game on first level
        Game game = new Game();
        game.setLevel(1);

        // when: trying to initialize level
        // then: exception is thrown
        assertThatIllegalStateException()
                .isThrownBy(game::initLevel)
                .withMessage("Level is already set");
    }

    @Test
    public void gamesWithTheSameIdShouldBeEqual() {
        // given: two games with the same database identifiers
        Game game = new Game();
        game.setId(1L);
        Game otherGame = new Game();
        otherGame.setId(1L);

        // when: checking for equality
        // then: they are equal
        assertThat(game).isEqualTo(otherGame);
    }

    @Test
    public void gamesWithDistinctIdsShouldNotBeEqual() {
        // given: two games with distinct database identifiers
        Game game = new Game();
        game.setId(1L);
        Game otherGame = new Game();
        otherGame.setId(2L);

        // when: checking for equality
        // then: they are not equal
        assertThat(game).isNotEqualTo(otherGame);
    }

    @Test
    public void gamesWithoutIdsShouldNotBeEqual() {
        // given: two games without database identifiers
        Game game = new Game();
        Game otherGame = new Game();

        // when: checking for equality
        // then: they are not equal
        assertThat(game).isNotEqualTo(otherGame);
    }
}
