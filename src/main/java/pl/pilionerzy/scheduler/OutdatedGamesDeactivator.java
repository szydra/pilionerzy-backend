package pl.pilionerzy.scheduler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import pl.pilionerzy.repository.GameRepository;

import java.time.LocalDateTime;

/**
 * This class contains a scheduler that marks games started at least <code>N</code> minutes ago as inactive,
 * where <code>N</code> is the value of <code>game.timeout</code> property or <code>N=60</code> by default.
 * The task is run once in a minute.
 */
@Component
@Slf4j
@RequiredArgsConstructor
class OutdatedGamesDeactivator {

    private final GameRepository gameRepository;

    @Value("${game.timeout:60}")
    private int gameTimeout;

    @Scheduled(fixedRate = 60_000)
    @Transactional
    public void deactivateOldGames() {
        int deactivatedGames = gameRepository.deactivateGamesStartedBefore(LocalDateTime.now().minusMinutes(gameTimeout));
        if (deactivatedGames > 0) {
            logger.info("{} games marked as inactive", deactivatedGames);
        } else {
            logger.debug("No timed out games found");
        }
    }
}
