package pl.pilionerzy.repository;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import pl.pilionerzy.model.Game;

import javax.validation.ConstraintViolationException;

import static org.apache.commons.lang3.RandomStringUtils.random;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static pl.pilionerzy.assertion.Assertions.assertThat;


@RunWith(SpringRunner.class)
@DataJpaTest
@ActiveProfiles("test")
public class GameRepositoryIntegrationTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private GameRepository gameRepository;

    @Test
    public void shouldNotSaveGameWithoutRequiredProperties() {
        var game = new Game();

        assertThatExceptionOfType(ConstraintViolationException.class)
                .isThrownBy(() -> entityManager.persistAndFlush(game))
                .satisfies(exception -> assertThat(exception)
                        .hasViolation("active", "game must be active or inactive")
                        .hasViolation("level", "game must have level")
                        .hasViolation("businessId", "game must have business id"));
    }

    @Test
    public void shouldDeactivateOldGame() {
        var game = insertActiveGame();

        var oneHourLater = game.getStartTime().plusMinutes(60);
        int deactivateGames = gameRepository.deactivateGamesStartedBefore(oneHourLater);

        assertThat(deactivateGames).isOne();
    }

    @Test
    public void shouldNotDeactivateNewGame() {
        var game = insertActiveGame();

        var fiveMinutesAgo = game.getStartTime().minusMinutes(5);
        int deactivateGames = gameRepository.deactivateGamesStartedBefore(fiveMinutesAgo);

        assertThat(deactivateGames).isZero();
    }

    @Test
    public void shouldFindByIdWithLastAskedQuestionWhenLastAskedQuestionDoesNotExits() {
        var game = insertActiveGame();

        entityManager.clear();
        var foundGame = gameRepository.findByIdWithLastQuestionAndAnswers(game.getId());

        assertThat(foundGame).isPresent();
    }

    private Game insertActiveGame() {
        var game = new Game();
        game.activate();
        game.setLevel(1);
        game.setBusinessId(random(32, "0123456789abcdef"));
        return entityManager.persistAndFlush(game);
    }
}
