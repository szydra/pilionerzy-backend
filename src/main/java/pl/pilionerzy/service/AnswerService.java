package pl.pilionerzy.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.pilionerzy.dto.GameDto;
import pl.pilionerzy.exception.GameException;
import pl.pilionerzy.exception.NoSuchGameException;
import pl.pilionerzy.mapping.DtoMapper;
import pl.pilionerzy.model.Game;
import pl.pilionerzy.model.Prefix;

import static pl.pilionerzy.util.GameUtils.getNextLevel;
import static pl.pilionerzy.util.GameUtils.validateForAnswer;

/**
 * Processes player's answers during a game.
 * Updates game level and marks game as inactive if necessary.
 */
@Service
@RequiredArgsConstructor
public class AnswerService {

    private final DtoMapper dtoMapper;
    private final GameService gameService;
    private final LevelService levelService;

    /**
     * Processes answer request, updates game status and returns game with the correct answer.
     *
     * @param gameId         game id
     * @param selectedPrefix answer selected by a player
     * @return game with prefix of the correct answer
     * @throws NoSuchGameException if no game with the passed id can be found
     * @throws GameException       if the existing game does not accept answers
     */
    @Transactional
    public GameDto doAnswer(Long gameId, Prefix selectedPrefix) {
        Game game = gameService.findByIdWithAskedQuestions(gameId);
        validateForAnswer(game);
        Prefix correct = game.getLastAskedQuestion().getCorrectAnswer().getPrefix();
        if (correct != selectedPrefix) {
            updateGameForIncorrect(game);
        } else {
            updateGameForCorrect(game);
        }
        return dtoMapper.mapToDto(game);
    }

    private void updateGameForCorrect(Game game) {
        int currentLevel = game.getLevel();
        game.setLevel(getNextLevel(currentLevel));
        if (levelService.isHighestLevel(game.getLevel())) {
            game.deactivate();
        }
    }

    private void updateGameForIncorrect(Game game) {
        int currentLevel = game.getLevel();
        game.deactivate();
        game.setLevel(levelService.getGuaranteedLevel(currentLevel));
    }
}
