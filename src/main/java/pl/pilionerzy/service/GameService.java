package pl.pilionerzy.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.pilionerzy.dto.GameDto;
import pl.pilionerzy.exception.GameException;
import pl.pilionerzy.exception.LifelineException;
import pl.pilionerzy.exception.NoSuchGameException;
import pl.pilionerzy.lifeline.LifelineProcessor;
import pl.pilionerzy.mapping.DtoMapper;
import pl.pilionerzy.model.Game;
import pl.pilionerzy.model.Lifeline;
import pl.pilionerzy.model.Question;
import pl.pilionerzy.repository.GameRepository;

import java.util.List;

import static pl.pilionerzy.util.GameUtils.isLifelineUsed;
import static pl.pilionerzy.util.GameUtils.validateForLifeline;

/**
 * Service that is responsible for basic operations on a game such as starting, stopping and providing lifelines.
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class GameService {

    private final DtoMapper mapper;
    private final GameRepository gameRepository;
    private final List<LifelineProcessor<?>> lifelineProcessors;

    /**
     * Creates and saves a new {@link Game} instance.
     *
     * @return started game
     */
    public GameDto startNewGame() {
        logger.debug("Starting a new game");
        Game game = new Game();
        game.activate();
        game.initLevel();
        Game startedGame = gameRepository.save(game);
        logger.debug("Started a game with id {}", startedGame.getId());
        return mapper.mapToDto(startedGame);
    }

    /**
     * Deactivates game with the passed id and returns it.
     * Should be called when a user manually stops the game.
     *
     * @param gameId game id
     * @return stopped game with the correct answer
     * @throws NoSuchGameException if no game with the passed id can be found
     * @throws GameException       if the game was already stopped
     */
    @Transactional
    public GameDto stopById(Long gameId) {
        logger.debug("Stopping the game with id {}", gameId);
        Game game = findByIdWithLastQuestionAndAnswers(gameId);
        try {
            game.deactivate();
        } catch (IllegalStateException e) {
            logger.warn("The game with id {} was already stopped", gameId);
            throw new GameException(e.getMessage());
        }
        return mapper.mapToDto(game);
    }

    /**
     * Processes the passed lifeline.
     *
     * @param gameId game id
     * @return lifeline result
     * @throws NoSuchGameException if no game with the passed id can be found
     * @throws LifelineException   if the passed lifeline was already used
     * @throws GameException       if the last asked question is null
     */
    @Transactional
    public Object processLifeline(Long gameId, Lifeline lifeline) {
        logger.debug("Applying {} lifeline to game with id {}", lifeline, gameId);
        Game game = findByIdWithUsedLifelines(gameId);
        validateForLifeline(game);
        if (isLifelineUsed(game, lifeline)) {
            logger.warn("Requested {} lifeline to the game with id {} for the second time", lifeline, gameId);
            throw new LifelineException(String.format("%s lifeline already used", lifeline));
        }
        return getLifelineProcessor(lifeline).process(game);
    }

    private LifelineProcessor<?> getLifelineProcessor(Lifeline lifeline) {
        return lifelineProcessors.stream()
                .filter(processor -> processor.type() == lifeline)
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("Cannot find processor for lifeline: " + lifeline));
    }

    Game findByIdWithAskedQuestions(Long gameId) {
        return gameRepository.findByIdWithAskedQuestions(gameId)
                .orElseThrow(() -> new NoSuchGameException(gameId));
    }

    Game findByIdWithUsedLifelines(Long gameId) {
        return gameRepository.findByIdWithUsedLifelines(gameId)
                .orElseThrow(() -> new NoSuchGameException(gameId));
    }

    Game findByIdWithLastQuestionAndAnswers(Long gameId) {
        return gameRepository.findByIdWithLastQuestionAndAnswers(gameId)
                .orElseThrow(() -> new NoSuchGameException(gameId));
    }

    void updateLastQuestion(Game game, Question question) {
        var askedQuestions = game.getAskedQuestions();
        askedQuestions.add(question);
        game.setLastAskedQuestion(question);
    }
}
