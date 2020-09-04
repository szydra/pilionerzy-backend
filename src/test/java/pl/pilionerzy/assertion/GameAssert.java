package pl.pilionerzy.assertion;

import org.assertj.core.api.AbstractObjectAssert;
import pl.pilionerzy.model.Game;

import java.util.Objects;

import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;

public class GameAssert extends AbstractObjectAssert<GameAssert, Game> {

    GameAssert(Game actual) {
        super(actual, GameAssert.class);
    }

    public GameAssert isActive() {
        isNotNull();

        if (!TRUE.equals(actual.getActive())) {
            failWithMessage("Expected to be active, but 'active' field was <%s>", actual.getActive());
        }

        return this;
    }

    public GameAssert isInactive() {
        isNotNull();

        if (!FALSE.equals(actual.getActive())) {
            failWithMessage("Expected to be inactive, but 'active' field was <%s>", actual.getActive());
        }

        return this;
    }

    public GameAssert hasLevel(int expectedLevel) {
        isNotNull();

        var actualLevel = actual.getLevel();
        if (!Objects.equals(actualLevel, expectedLevel)) {
            failWithMessage("Expected to have level <%s>, but was <%s>", expectedLevel, actualLevel);
        }

        return this;
    }
}
