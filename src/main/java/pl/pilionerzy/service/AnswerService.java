package pl.pilionerzy.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pl.pilionerzy.model.Game;
import pl.pilionerzy.util.LevelUtils;

@Service
public class AnswerService {

    private GameService gameService;

    @Autowired
    public AnswerService(GameService gameService) {
        this.gameService = gameService;
    }

    public Character processRequest(Long gameId, Character selectedPrefix) {
        Game game = gameService.findById(gameId);
        Character correct = gameService.getCorrectAnswerPrefix(game);
        if (!correct.equals(selectedPrefix)) {
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
        game.setLevel(LevelUtils.getQuaranteedLevel(currentLevel));
    }

}
