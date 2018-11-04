package pl.pilionerzy.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pl.pilionerzy.dao.GameDao;
import pl.pilionerzy.exception.GameException;
import pl.pilionerzy.exception.NoSuchGameException;
import pl.pilionerzy.model.Game;
import pl.pilionerzy.model.Prefix;
import pl.pilionerzy.model.Question;

import java.util.Set;

@Service
public class GameService {

    private GameDao gameDao;

    @Autowired
    public GameService(GameDao gameDao) {
        this.gameDao = gameDao;
    }

    public Game startNewGame() {
        Game game = new Game();
        game.setActive(true);
        game.setLevel(0);
        return save(game);
    }

    public Game stopGame(Long gameId) {
        Game game = findById(gameId);
        game.setActive(false);
        return save(game);
    }

    public Prefix stopAndGetCorrectAnswerPrefix(Long gameId){
        Game game = stopGame(gameId);
        return getCorrectAnswerPrefix(game);
    }

    Game findById(Long gameId) {
        return gameDao.findById(gameId)
                .orElseThrow(() -> new NoSuchGameException(gameId));
    }

    Game save(Game game) {
        return gameDao.save(game);
    }

    public Prefix getCorrectAnswerPrefix(Game game) {
        Long lastAskedQuestionId = game.getLastAskedQuestionId();
        return game.getAskedQuestions().stream()
                .filter(q -> lastAskedQuestionId.equals(q.getId()))
                .map(Question::getCorrectAnswer)
                .findAny()
                .orElseThrow(() -> new GameException("Game does not have last asked question"));
    }

    void updateLastQuestion(Game game, Question question) {
        Set<Question> askedQuestions = game.getAskedQuestions();
        askedQuestions.add(question);
        game.setAskedQuestions(askedQuestions);
        game.setLastAskedQuestionId(question.getId());
        save(game);
    }

}
