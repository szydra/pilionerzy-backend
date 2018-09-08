package pl.pilionerzy.util;

import pl.pilionerzy.exception.GameException;
import pl.pilionerzy.model.Game;

public class GameUtils {

    public static void validate(Game game, RequestType requestType) {
        if (Boolean.FALSE.equals(game.getActive())) {
            throw new GameException(String.format("Game with id %s is inactive", game.getId()));
        }
        boolean valid;
        switch (requestType) {
            case ANSWER:
                valid = game.getAskedQuestions().size() == game.getLevel() + 1;
                break;
            case QUESTION:
                valid = game.getAskedQuestions().size() == game.getLevel();
                break;
            default:
                valid = false;
        }
        if (!valid) {
            throw new GameException("Invalid number of requests for game with id " + game.getId());
        }
    }

}
