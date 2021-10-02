package pl.pilionerzy.lifeline;

import pl.pilionerzy.model.*;

import java.util.Collection;

import static pl.pilionerzy.util.GameUtils.getRejectedAnswers;

abstract class AbstractLifelineProcessor<T> implements LifelineProcessor<T> {

    @Override
    public T process(Game game) {
        updateUsedLifelines(game, type());
        return getResult(game.getLastAskedQuestion(), getRejectedAnswers(game));
    }

    protected void updateUsedLifelines(Game game, Lifeline lifeline) {
        var usedLifelines = game.getUsedLifelines();
        var usedLifeline = new UsedLifeline();
        usedLifeline.setType(lifeline);
        usedLifeline.setQuestion(game.getLastAskedQuestion());
        usedLifelines.add(usedLifeline);
    }

    protected abstract T getResult(Question question, Collection<Prefix> rejectedAnswers);
}
