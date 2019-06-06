package pl.pilionerzy.model;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalStateException;

public class GameTest {

    private Game game = new Game();

    @Test
    public void shouldActivateInactiveGame() {
        game.activate();

        assertThat(game.getActive()).isTrue();
    }

    @Test
    public void shouldThrowExceptionWhenActivatingActiveGame() {
        game.setActive(true);

        assertThatIllegalStateException().isThrownBy(() -> game.activate());
    }

    @Test
    public void shouldDeactivateActiveGame() {
        game.setActive(true);

        game.deactivate();

        assertThat(game.getActive()).isFalse();
    }

    @Test
    public void shouldThrowExceptionWhenDeactivatingInactiveGame() {
        game.setActive(false);

        assertThatIllegalStateException().isThrownBy(() -> game.deactivate());
    }

    @Test
    public void shouldInitLevelInANewGame() {
        game.initLevel();

        assertThat(game.getLevel()).isZero();
    }

    @Test
    public void shouldThrowExceptionWhenLevelIsInitialized() {
        game.setLevel(1);

        assertThatIllegalStateException().isThrownBy(() -> game.initLevel());
    }
}
