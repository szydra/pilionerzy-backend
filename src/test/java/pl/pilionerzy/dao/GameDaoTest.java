package pl.pilionerzy.dao;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import pl.pilionerzy.model.Game;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;


@RunWith(SpringRunner.class)
@DataJpaTest
@ActiveProfiles("test")
public class GameDaoTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private GameDao gameDao;

    @Test
    public void shouldDeactivateOldGame() {
        Game game = insertActiveGame();

        LocalDateTime startTime = game.getStartTime();
        int deactivateGames = gameDao.deactivateGamesStartedBefore(startTime.plusMinutes(60));

        assertThat(deactivateGames).isOne();
    }

    @Test
    public void shouldNotDeactivateNewGame() {
        Game game = insertActiveGame();

        LocalDateTime startTime = game.getStartTime();
        int deactivateGames = gameDao.deactivateGamesStartedBefore(startTime.minusMinutes(5));

        assertThat(deactivateGames).isZero();
    }

    private Game insertActiveGame() {
        Game game = new Game();
        game.setActive(true);
        game.setLevel(1);
        return entityManager.persistAndFlush(game);
    }

}
