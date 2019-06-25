package pl.pilionerzy.service;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.pilionerzy.dao.QuestionDao;
import pl.pilionerzy.exception.GameException;
import pl.pilionerzy.exception.NoSuchGameException;
import pl.pilionerzy.exception.NotEnoughDataException;
import pl.pilionerzy.model.Game;
import pl.pilionerzy.model.Question;
import pl.pilionerzy.util.GameUtils;
import pl.pilionerzy.util.RequestType;

import java.util.Random;
import java.util.Set;

/**
 * Service that is responsible for operations on questions such as saving or drawing.
 */
@Service
public class QuestionService {

    /**
     * The number of attempts to get another question from the database.
     */
    static final int LIMIT = 12;

    private GameService gameService;
    private QuestionDao questionDao;

    public QuestionService(QuestionDao questionDao, GameService gameService) {
        this.questionDao = questionDao;
        this.gameService = gameService;
    }

    public Question save(Question question) {
        return questionDao.save(question);
    }

    /**
     * Draws another question to be asked for an active game.
     *
     * @param gameId game id
     * @return next question
     * @throws NoSuchGameException    if no game with the passed id can be found
     * @throws GameException          if it is not allow to fetch another question
     * @throws NotEnoughDataException if fetching another question failed
     */
    @Transactional
    public Question getNextQuestionByGameId(Long gameId) {
        Game game = gameService.findById(gameId);
        GameUtils.validate(game, RequestType.QUESTION);
        return getAnotherQuestion(game);
    }

    private Question getAnotherQuestion(Game game) {
        Set<Question> askedQuestions = game.getAskedQuestions();
        Question question;
        int attempts = 0;
        do {
            // Prevent an infinite loop
            if (attempts++ >= LIMIT) {
                throw new NotEnoughDataException("Cannot get another question");
            }
            question = getRandomQuestion();
        } while (askedQuestions.contains(question));
        gameService.updateLastQuestion(game, question);
        return question;
    }

    private Question getRandomQuestion() {
        Random random = new Random();
        int page = random.nextInt((int) questionDao.countByActive(true));
        Slice<Question> questionPage = questionDao.findByActive(true, PageRequest.of(page, 1));
        if (questionPage.hasContent()) {
            return questionPage.getContent().get(0);
        } else {
            throw new NotEnoughDataException("Cannot get another question");
        }
    }
}
