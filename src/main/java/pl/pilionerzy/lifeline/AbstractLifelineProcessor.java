package pl.pilionerzy.lifeline;

import pl.pilionerzy.lifeline.model.FiftyFiftyResult;
import pl.pilionerzy.model.*;

import java.util.Collection;

import static pl.pilionerzy.util.GameUtils.getRejectedAnswers;

abstract class AbstractLifelineProcessor<T> implements LifelineProcessor<T> {

    @Override
    public T process(Game game) {
        var result = getResult(game.getLastAskedQuestion(), getRejectedAnswers(game));
        updateUsedLifelines(game, type(), result);
        return result;
    }

    private void updateUsedLifelines(Game game, Lifeline lifeline, T result) {
        var usedLifelines = game.getUsedLifelines();
        var usedLifeline = new UsedLifeline();
        usedLifeline.setType(lifeline);
        usedLifeline.setQuestion(game.getLastAskedQuestion());
        saveResult(usedLifeline, result);
        usedLifelines.add(usedLifeline);
    }

    private void saveResult(UsedLifeline usedLifeline, T result) {
        // TODO Save results for all lifelines
        if (result instanceof FiftyFiftyResult) {
            usedLifeline.setRejectedAnswers(((FiftyFiftyResult) result).getPrefixesToDiscard());
        }
    }

    protected abstract T getResult(Question question, Collection<Prefix> rejectedAnswers);
}
