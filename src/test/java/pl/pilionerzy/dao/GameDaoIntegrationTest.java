package pl.pilionerzy.dao;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import pl.pilionerzy.model.Game;

import javax.validation.ConstraintViolationException;
import java.time.LocalDateTime;

import static org.apache.commons.lang3.RandomStringUtils.random;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static pl.pilionerzy.assertion.Assertions.assertThat;


@RunWith(SpringRunner.class)
@DataJpaTest
@ActiveProfiles("test")
public class GameDaoIntegrationTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private GameDao gameDao;

    @Test
    public void shouldNotSaveGameWithoutRequiredProperties() {
        Game game = new Game();

        assertThatExceptionOfType(ConstraintViolationException.class)
                .isThrownBy(() -> entityManager.persistAndFlush(game))
                .satisfies(exception -> assertThat(exception)
                        .hasViolation("active", "game must be active or inactive")
                        .hasViolation("level", "game must have level")
                        .hasViolation("businessId", "game must have business id"));
    }

    @Test
    public void shouldDeactivateOldGame() {
        Game game = insertActiveGame();

        LocalDateTime oneHourLater = game.getStartTime().plusMinutes(60);
        int deactivateGames = gameDao.deactivateGamesStartedBefore(oneHourLater);

        assertThat(deactivateGames).isOne();
    }

    @Test
    public void shouldNotDeactivateNewGame() {
        Game game = insertActiveGame();

        LocalDateTime fiveMinutesAgo = game.getStartTime().minusMinutes(5);
        int deactivateGames = gameDao.deactivateGamesStartedBefore(fiveMinutesAgo);

        assertThat(deactivateGames).isZero();
    }

    private Game insertActiveGame() {
        Game game = new Game();
        game.activate();
        game.setLevel(1);
        game.setBusinessId(random(32, "0123456789abcdef"));
        return entityManager.persistAndFlush(game);
    }
}
