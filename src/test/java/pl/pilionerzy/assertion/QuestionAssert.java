package pl.pilionerzy.assertion;

import org.assertj.core.api.AbstractObjectAssert;
import pl.pilionerzy.model.Question;

import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;

public class QuestionAssert extends AbstractObjectAssert<QuestionAssert, Question> {

    QuestionAssert(Question actual) {
        super(actual, QuestionAssert.class);
    }

    public QuestionAssert isActive() {
        isNotNull();

        if (!TRUE.equals(actual.getActive())) {
            failWithMessage("Expected to be active, but 'active' field was <%s>", actual.getActive());
        }

        return this;
    }

    public QuestionAssert isInactive() {
        isNotNull();

        if (!FALSE.equals(actual.getActive())) {
            failWithMessage("Expected to be inactive, but 'active' field was <%s>", actual.getActive());
        }

        return this;
    }
}
