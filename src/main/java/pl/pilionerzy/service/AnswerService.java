package pl.pilionerzy.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.pilionerzy.exception.GameException;
import pl.pilionerzy.exception.NoSuchGameException;
import pl.pilionerzy.model.Game;
import pl.pilionerzy.model.Prefix;
import pl.pilionerzy.util.RequestType;

import static pl.pilionerzy.util.GameUtils.validate;
import static pl.pilionerzy.util.LevelUtils.*;

/**
 * Processes player's answers during a game.
 * Updates game level and marks game as inactive if necessary.
 */
@Service
@RequiredArgsConstructor
public class AnswerService {

    private final GameService gameService;

    /**
     * Processes answer request, updates game status and returns correct answer.
     *
     * @param gameId         game id
     * @param selectedPrefix answer selected by a player
     * @return prefix of the correct answer
     * @throws NoSuchGameException if no game with the passed id can be found
     * @throws GameException       if the existing game does not accept answers
     */
    @Transactional
    public Prefix doAnswer(Long gameId, Prefix selectedPrefix) {
        Game game = gameService.findById(gameId);
        validate(game, RequestType.ANSWER);
        Prefix correct = game.getLastAskedQuestion().getCorrectAnswer();
        if (correct != selectedPrefix) {
            updateGameForIncorrect(game);
        } else {
            updateGameForCorrect(game);
        }
        return correct;
    }

    private void updateGameForCorrect(Game game) {
        int currentLevel = game.getLevel();
        game.setLevel(getNextLevel(currentLevel));
        if (isHighestLevel(game.getLevel())) {
            game.deactivate();
        }
    }

    private void updateGameForIncorrect(Game game) {
        int currentLevel = game.getLevel();
        game.deactivate();
        game.setLevel(getGuaranteedLevel(currentLevel));
    }
}
