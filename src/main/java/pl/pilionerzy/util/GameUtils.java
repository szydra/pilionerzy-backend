package pl.pilionerzy.util;

import pl.pilionerzy.exception.GameException;
import pl.pilionerzy.model.Game;
import pl.pilionerzy.model.Prefix;
import pl.pilionerzy.model.Question;

import java.util.Optional;

public class GameUtils {

    public static void validate(Game game, RequestType requestType) {
        if (Boolean.FALSE.equals(game.getActive())) {
            throw new GameException(String.format("Game with id %s is inactive", game.getId()));
        }
        boolean valid;
        switch (requestType) {
            case ANSWER:
                valid = true;
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

    public static Prefix getCorrectAnswerPrefix(Game game) {
        return Optional.ofNullable(game.getLastAskedQuestion())
                .map(Question::getCorrectAnswer)
                .orElseThrow(() -> new GameException("Game does not have last asked question"));
    }

}
