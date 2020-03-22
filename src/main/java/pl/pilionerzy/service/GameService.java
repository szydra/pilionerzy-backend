package pl.pilionerzy.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.pilionerzy.dao.GameDao;
import pl.pilionerzy.dto.GameDto;
import pl.pilionerzy.exception.GameException;
import pl.pilionerzy.exception.LifelineException;
import pl.pilionerzy.exception.NoSuchGameException;
import pl.pilionerzy.lifeline.Calculator;
import pl.pilionerzy.lifeline.model.AudienceAnswer;
import pl.pilionerzy.lifeline.model.FiftyFiftyResult;
import pl.pilionerzy.lifeline.model.FriendsAnswer;
import pl.pilionerzy.mapping.DtoMapper;
import pl.pilionerzy.model.*;

import java.util.Collection;
import java.util.List;
import java.util.Set;

import static pl.pilionerzy.model.Lifeline.*;
import static pl.pilionerzy.util.GameUtils.*;
import static pl.pilionerzy.util.RequestType.LIFELINE;

/**
 * Service that is responsible for basic operations on a game such as starting, stopping and providing lifelines.
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class GameService {

    private final Calculator lifelineCalculator;
    private final DtoMapper mapper;
    private final GameDao gameDao;
    private final GameIdGenerator gameIdGenerator;

    /**
     * Creates and saves a new {@link Game} instance.
     *
     * @return started game
     */
    public GameDto startNewGame() {
        logger.debug("Starting a new game");
        Game game = new Game();
        game.setBusinessId(gameIdGenerator.generate());
        game.activate();
        game.initLevel();
        Game startedGame = gameDao.save(game);
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
     * Processes fifty-fifty lifeline.
     *
     * @param gameId game id
     * @return prefixes to reject
     * @throws NoSuchGameException if no game with the passed id can be found
     * @throws LifelineException   if fifty-fifty was already used
     * @throws GameException       if the last asked question is null
     */
    @Transactional
    public Collection<Prefix> getTwoIncorrectPrefixes(Long gameId) {
        logger.debug("Applying fifty-fifty lifeline to game with id {}", gameId);
        Game game = findById(gameId);
        validate(game, LIFELINE);
        if (isLifelineUsed(game, FIFTY_FIFTY)) {
            logger.warn("Requested fifty-fifty lifeline to the game with id {} for the second time", gameId);
            throw new LifelineException("Fifty-fifty lifeline already used");
        }
        updateUsedLifelines(game, FIFTY_FIFTY);
        FiftyFiftyResult fiftyFifty = lifelineCalculator.getFiftyFiftyResult(game.getLastAskedQuestion());
        updateRejectedAnswers(game, fiftyFifty.getPrefixesToDiscard());
        return fiftyFifty.getPrefixesToDiscard();
    }

    /**
     * Processes phone-a-friend lifeline.
     *
     * @param gameId game id
     * @return friend's answer containing prefix and wisdom
     * @throws NoSuchGameException if no game with the passed id can be found
     * @throws LifelineException   if phone-a-friend was already used
     * @throws GameException       if the last asked question is null
     */
    @Transactional
    public FriendsAnswer getFriendsAnswerByGameId(Long gameId) {
        logger.debug("Applying phone-a-friend lifeline to game with id {}", gameId);
        Game game = findById(gameId);
        validate(game, LIFELINE);
        if (isLifelineUsed(game, PHONE_A_FRIEND)) {
            logger.warn("Requested phone-a-friend lifeline to the game with id {} for the second time", gameId);
            throw new LifelineException("Phone a friend lifeline already used");
        }
        updateUsedLifelines(game, PHONE_A_FRIEND);
        return lifelineCalculator.getFriendsAnswer(game.getLastAskedQuestion(), getRejectedAnswers(game));
    }

    /**
     * Processes ask-the-audience lifeline.
     *
     * @param gameId game id
     * @return audience answers sorted by prefix
     * @throws NoSuchGameException if no game with the passed id can be found
     * @throws LifelineException   if ask-the-audience was already used
     * @throws GameException       if the last asked question is null
     */
    @Transactional
    public AudienceAnswer getAudienceAnswerByGameId(Long gameId) {
        logger.debug("Applying ask-the-audience lifeline to game with id {}", gameId);
        Game game = findById(gameId);
        validate(game, LIFELINE);
        if (isLifelineUsed(game, ASK_THE_AUDIENCE)) {
            logger.warn("Requested ask-the-audience lifeline to the game with id {} for the second time", gameId);
            throw new LifelineException("Ask the audience lifeline already used");
        }
        updateUsedLifelines(game, ASK_THE_AUDIENCE);
        return lifelineCalculator.getAudienceAnswer(game.getLastAskedQuestion(), getRejectedAnswers(game));
    }

    Game findById(Long gameId) {
        return gameDao.findById(gameId)
                .orElseThrow(() -> new NoSuchGameException(gameId));
    }

    Game findByIdWithLastQuestionAndAnswers(Long gameId) {
        return gameDao.findByIdWithLastQuestionAndAnswers(gameId)
                .orElseThrow(() -> new NoSuchGameException(gameId));
    }

    private void updateUsedLifelines(Game game, Lifeline lifeline) {
        List<UsedLifeline> usedLifelines = game.getUsedLifelines();
        UsedLifeline usedLifeline = new UsedLifeline();
        usedLifeline.setType(lifeline);
        usedLifeline.setQuestion(game.getLastAskedQuestion());
        usedLifelines.add(usedLifeline);
    }

    private void updateRejectedAnswers(Game game, Collection<Prefix> rejectedAnswers) {
        game.getUsedLifelines().forEach(
                usedLifeline -> {
                    if (usedLifeline.getType() == FIFTY_FIFTY) {
                        usedLifeline.setRejectedAnswers(rejectedAnswers);
                    }
                });
    }

    void updateLastQuestion(Game game, Question question) {
        Set<Question> askedQuestions = game.getAskedQuestions();
        askedQuestions.add(question);
        game.setLastAskedQuestion(question);
    }
}
