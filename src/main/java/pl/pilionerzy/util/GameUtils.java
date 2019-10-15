package pl.pilionerzy.util;

import pl.pilionerzy.exception.GameException;
import pl.pilionerzy.model.Game;
import pl.pilionerzy.model.Lifeline;
import pl.pilionerzy.model.Prefix;
import pl.pilionerzy.model.UsedLifeline;

import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import static java.lang.Boolean.FALSE;
import static pl.pilionerzy.model.Lifeline.FIFTY_FIFTY;
import static pl.pilionerzy.util.LevelUtils.getNextLevel;

public class GameUtils {

    public static void validate(Game game, RequestType requestType) {
        if (FALSE.equals(game.getActive())) {
            throw new GameException(String.format("Game with id %s is inactive", game.getId()));
        }
        switch (requestType) {
            case ANSWER:
                if (game.getLastAskedQuestion() == null) {
                    throw new GameException("Game does not have last asked question");
                } else if (game.getAskedQuestions().size() != getNextLevel(game.getLevel())) {
                    throw new GameException("Invalid number of requests for game with id " + game.getId());
                }
                break;
            case LIFELINE:
                if (game.getLastAskedQuestion() == null) {
                    throw new GameException("Cannot use a lifeline for a game without last asked question");
                }
                break;
            case QUESTION:
                if (game.getAskedQuestions().size() != game.getLevel()) {
                    throw new GameException("Invalid number of requests for game with id " + game.getId());
                }
                break;
            default:
                throw new GameException("Unknown request type detected");
        }
    }

    public static boolean isLifelineUsed(Game game, Lifeline lifeline) {
        return game.getUsedLifelines().stream()
                .map(UsedLifeline::getType)
                .anyMatch(lifeline::equals);
    }

    public static Set<Prefix> getRejectedAnswers(Game game) {
        return game.getUsedLifelines().stream()
                .filter(lifeline -> lifeline.getType() == FIFTY_FIFTY)
                .filter(lifeline -> Objects.equals(lifeline.getQuestion(), game.getLastAskedQuestion()))
                .flatMap(lifeline -> lifeline.getRejectedAnswers().stream())
                .collect(Collectors.toSet());
    }
}
