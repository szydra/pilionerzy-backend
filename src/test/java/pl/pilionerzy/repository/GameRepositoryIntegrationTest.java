package pl.pilionerzy.repository;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import pl.pilionerzy.model.Game;

import javax.validation.ConstraintViolationException;

import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static pl.pilionerzy.assertion.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
@DataJpaTest
@ActiveProfiles("test")
class GameRepositoryIntegrationTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private GameRepository gameRepository;

    @Test
    void shouldNotSaveGameWithoutRequiredProperties() {
        // given: game without required properties
        var game = new Game();

        // when: trying to save the game
        // then: constraints are violated
        assertThatExceptionOfType(ConstraintViolationException.class)
                .isThrownBy(() -> entityManager.persistAndFlush(game))
                .satisfies(exception -> assertThat(exception)
                        .hasViolation("active", "game must be active or inactive")
                        .hasViolation("level", "game must have level"));
    }

    @Test
    void shouldDeactivateOldGame() {
        // given: active game
        var game = insertActiveGame();

        // when: deactivating games started before a moment in the future
        var oneHourLater = game.getStartTime().plusMinutes(60);
        int deactivateGames = gameRepository.deactivateGamesStartedBefore(oneHourLater);

        // then: the game was deactivated
        assertThat(deactivateGames).isOne();
    }

    @Test
    void shouldNotDeactivateNewGame() {
        // given: active game
        var game = insertActiveGame();

        // when: deactivating games started before a moment in the past
        var fiveMinutesAgo = game.getStartTime().minusMinutes(5);
        int deactivateGames = gameRepository.deactivateGamesStartedBefore(fiveMinutesAgo);

        // then: the game was not deactivated
        assertThat(deactivateGames).isZero();
    }

    private Game insertActiveGame() {
        var game = new Game();
        game.activate();
        game.setLevel(1);
        return entityManager.persistAndFlush(game);
    }
}
