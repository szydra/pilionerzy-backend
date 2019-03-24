package pl.pilionerzy.util.lifeline;

import org.junit.Before;
import org.junit.Test;
import pl.pilionerzy.model.Prefix;
import pl.pilionerzy.model.Question;

import static org.assertj.core.api.Assertions.assertThat;
import static pl.pilionerzy.util.lifeline.FiftyFiftyCalculator.getPrefixesToDiscard;

public class FiftyFiftyCalculatorTest {

    private Question question;

    @Before
    public void initQuestion() {
        question = new Question();
        question.setCorrectAnswer(Prefix.A);
    }

    @Test
    public void shouldDiscardExactlyTwoIncorrectAnswers() {
        assertThat(getPrefixesToDiscard(question))
                .hasSize(2)
                .doesNotContain(Prefix.A);
    }
}
