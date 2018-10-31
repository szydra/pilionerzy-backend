package pl.pilionerzy.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pl.pilionerzy.model.Game;
import pl.pilionerzy.model.Prefix;
import pl.pilionerzy.util.GameUtils;
import pl.pilionerzy.util.LevelUtils;
import pl.pilionerzy.util.RequestType;

@Service
public class AnswerService {

    private GameService gameService;

    @Autowired
    public AnswerService(GameService gameService) {
        this.gameService = gameService;
    }

    public Prefix processRequest(Long gameId, Prefix selectedPrefix) {
        Game game = gameService.findById(gameId);
        GameUtils.validate(game, RequestType.ANSWER);
        Prefix correct = gameService.getCorrectAnswerPrefix(game);
        if (correct != selectedPrefix) {
            updateGameForIncorrect(game);
        } else {
            updateGameForCorrect(game);
        }
        gameService.save(game);
        return correct;
    }

    private void updateGameForCorrect(Game game) {
        int currentLevel = game.getLevel();
        game.setLevel(LevelUtils.getNextLevel(currentLevel));
        if (LevelUtils.isHighestLevel(game.getLevel())) {
            game.setActive(false);
        }
    }

    private void updateGameForIncorrect(Game game) {
        int currentLevel = game.getLevel();
        game.setActive(false);
        game.setLevel(LevelUtils.getGuaranteedLevel(currentLevel));
    }

}
