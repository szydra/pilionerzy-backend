package pl.pilionerzy.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import pl.pilionerzy.dao.QuestionDao;
import pl.pilionerzy.exception.GameException;
import pl.pilionerzy.model.Game;
import pl.pilionerzy.model.Question;
import pl.pilionerzy.util.GameUtils;

import java.util.Random;
import java.util.Set;

@Service
public class QuestionService {

    private GameService gameService;

    private QuestionDao questionDao;

    @Autowired
    public QuestionService(QuestionDao questionDao, GameService gameService) {
        this.questionDao = questionDao;
        this.gameService = gameService;
    }

    public Question save(Question question) {
        return questionDao.save(question);
    }

    public Question getNextQuestion(Long gameId) {
        Game game = gameService.findById(gameId);
        GameUtils.validate(game);
        return getAnotherQuestion(game);
    }

    private Question getAnotherQuestion(Game game) {
        Set<Question> askedQuestions = game.getAskedQuestions();
        Question question;
        do {
            question = getRandomQuestion();
        } while (askedQuestions.contains(question));
        gameService.updateLastQuestion(game, question);
        return question;
    }

    private Question getRandomQuestion() {
        Random random = new Random();
        int page = random.nextInt((int) questionDao.count());
        Page<Question> questionPage = questionDao.findAll(PageRequest.of(page, 1));
        if (questionPage.hasContent()) {
            return questionPage.getContent().get(0);
        } else {
            throw new GameException("Cannot get another question");
        }
    }

}
