package pl.pilionerzy.util;

import pl.pilionerzy.exception.GameException;
import pl.pilionerzy.model.Game;

public class GameUtils {

    public static void validate(Game game) {
        if (Boolean.FALSE.equals(game.getActive())) {
            throw new GameException(String.format("Game with id %s is inactive", game.getId()));
        }
        if (game.getAskedQuestions().size() != game.getLevel()) {
            throw new GameException("Too many requests for game with id " + game.getId());
        }
    }

}
