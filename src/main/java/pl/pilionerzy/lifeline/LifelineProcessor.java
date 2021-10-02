package pl.pilionerzy.lifeline;

import pl.pilionerzy.model.Game;
import pl.pilionerzy.model.Lifeline;

public interface LifelineProcessor<T> {

    Lifeline type();

    T process(Game game);
}
