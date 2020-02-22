package pl.pilionerzy.lifeline;

import org.junit.Before;
import org.junit.Test;
import pl.pilionerzy.model.Answer;
import pl.pilionerzy.model.Prefix;
import pl.pilionerzy.model.Question;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class FiftyFiftyCalculatorTest {

    private FiftyFiftyCalculator calculator = new FiftyFiftyCalculator();
    private Question question;

    @Before
    public void initQuestion() {
        question = new Question();
        Answer answer = new Answer();
        answer.setPrefix(Prefix.A);
        answer.setCorrect(true);
        question.setAnswers(List.of(answer));
    }

    @Test
    public void shouldDiscardExactlyTwoIncorrectAnswers() {
        assertThat(calculator.getPrefixesToDiscard(question).getPrefixesToDiscard())
                .hasSize(2)
                .doesNotContain(Prefix.A);
    }
}
