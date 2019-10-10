package pl.pilionerzy.assertion;

import pl.pilionerzy.model.Game;
import pl.pilionerzy.model.Question;

import javax.validation.ConstraintViolationException;

public class Assertions extends org.assertj.core.api.Assertions {

    public static ConstraintViolationExceptionAssert assertThat(ConstraintViolationException actual) {
        return new ConstraintViolationExceptionAssert(actual);
    }

    public static GameAssert assertThat(Game actual) {
        return new GameAssert(actual);
    }

    public static QuestionAssert assertThat(Question actual) {
        return new QuestionAssert(actual);
    }
}
