package pl.pilionerzy.scheduler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import pl.pilionerzy.dao.GameDao;

import java.time.LocalDateTime;

/**
 * This class contains a scheduler that marks games started at least <code>N</code> minutes ago as inactive,
 * where <code>N</code> is the value of <code>game.timeout</code> property or <code>N=60</code> by default.
 * The task is run once in a minute.
 */
@Component
class OutdatedGamesDeactivator {

    private static final Logger LOGGER = LoggerFactory.getLogger(OutdatedGamesDeactivator.class);

    private GameDao gameDao;

    @Value("${game.timeout:60}")
    private int gameTimeout;

    @Autowired
    public OutdatedGamesDeactivator(GameDao gameDao) {
        this.gameDao = gameDao;
    }

    @Scheduled(fixedRate = 60_000)
    @Transactional
    public void deactivateOldGames() {
        int deactivatedGames = gameDao.deactivateGamesStartedBefore(LocalDateTime.now().minusMinutes(gameTimeout));
        if (deactivatedGames > 0) {
            LOGGER.info("{} games marked as inactive", deactivatedGames);
        }
    }

}
