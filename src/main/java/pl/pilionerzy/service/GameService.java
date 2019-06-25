package pl.pilionerzy.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.pilionerzy.dao.GameDao;
import pl.pilionerzy.dto.GameDto;
import pl.pilionerzy.exception.GameException;
import pl.pilionerzy.exception.LifelineException;
import pl.pilionerzy.exception.NoSuchGameException;
import pl.pilionerzy.mapping.DtoMapper;
import pl.pilionerzy.model.*;
import pl.pilionerzy.util.lifeline.AskTheAudienceCalculator;
import pl.pilionerzy.util.lifeline.FiftyFiftyCalculator;

import java.util.*;
import java.util.stream.Collectors;

import static pl.pilionerzy.model.Lifeline.ASK_THE_AUDIENCE;
import static pl.pilionerzy.model.Lifeline.FIFTY_FIFTY;

/**
 * Service that is responsible for basic operations on a game such as starting, stopping and providing lifelines.
 */
@Service
public class GameService {

    private DtoMapper mapper;
    private GameDao gameDao;

    public GameService(DtoMapper mapper, GameDao gameDao) {
        this.mapper = mapper;
        this.gameDao = gameDao;
    }

    /**
     * Creates and saves a new {@link Game} instance.
     *
     * @return started game
     */
    public GameDto startNewGame() {
        Game game = new Game();
        game.activate();
        game.initLevel();
        return mapper.mapToDto(gameDao.save(game));
    }

    /**
     * Deactivates game with the passed id and returns the correct answer.
     * Should be called when a user manually stops the game.
     *
     * @param gameId game id
     * @return prefix of the correct answer
     * @throws NoSuchGameException if no game with the passed id can be found
     */
    @Transactional
    public GameDto stopById(Long gameId) {
        Game game = findById(gameId);
        game.deactivate();
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
        Game game = findById(gameId);
        Collection<UsedLifeline> usedLifelines = game.getUsedLifelines();
        if (usedLifelines.stream()
                .map(UsedLifeline::getType)
                .anyMatch(type -> type == FIFTY_FIFTY)) {
            throw new LifelineException("Fifty-fifty lifeline already used");
        }
        UsedLifeline fiftyFifty = getFiftyFiftyResult(game);
        usedLifelines.add(fiftyFifty);
        return fiftyFifty.getRejectedAnswers();
    }

    private UsedLifeline getFiftyFiftyResult(Game game) {
        UsedLifeline usedLifeline = new UsedLifeline();
        usedLifeline.setType(FIFTY_FIFTY);
        usedLifeline.setQuestion(game.getLastAskedQuestion());
        Collection<Prefix> rejectedAnswers = Optional.ofNullable(game.getLastAskedQuestion())
                .map(FiftyFiftyCalculator::getPrefixesToDiscard)
                .orElseThrow(() -> new GameException("Cannot use lifeline for a game without last asked question"));
        usedLifeline.setRejectedAnswers(rejectedAnswers);
        return usedLifeline;
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
    public Map<Prefix, AudienceAnswer> getAudienceAnswerByGameId(Long gameId) {
        Game game = findById(gameId);
        Collection<UsedLifeline> usedLifelines = game.getUsedLifelines();
        if (usedLifelines.stream()
                .map(UsedLifeline::getType)
                .anyMatch(type -> type == ASK_THE_AUDIENCE)) {
            throw new LifelineException("Ask the audience lifeline already used");
        }
        UsedLifeline askTheAudience = new UsedLifeline();
        askTheAudience.setType(ASK_THE_AUDIENCE);
        askTheAudience.setQuestion(game.getLastAskedQuestion());
        usedLifelines.add(askTheAudience);
        return getAudienceAnswer(game);
    }

    private Map<Prefix, AudienceAnswer> getAudienceAnswer(Game game) {
        Question lastAskedQuestion = Optional.ofNullable(game.getLastAskedQuestion())
                .orElseThrow(() -> new GameException("Cannot use lifeline for a game without last asked question"));
        Set<Prefix> rejectedAnswers = game.getUsedLifelines().stream()
                .filter(usedLifeline -> usedLifeline.getType() == FIFTY_FIFTY)
                .filter(usedLifeline -> Objects.equals(usedLifeline.getQuestion(), lastAskedQuestion))
                .map(UsedLifeline::getRejectedAnswers)
                .flatMap(Collection::stream)
                .collect(Collectors.toSet());
        return new AskTheAudienceCalculator().getAnswer(lastAskedQuestion, rejectedAnswers);
    }

    Game findById(Long gameId) {
        return gameDao.findById(gameId)
                .orElseThrow(() -> new NoSuchGameException(gameId));
    }

    void updateLastQuestion(Game game, Question question) {
        Set<Question> askedQuestions = game.getAskedQuestions();
        askedQuestions.add(question);
        game.setLastAskedQuestion(question);
    }
}
