package pl.pilionerzy.util.lifeline;

import org.junit.Before;
import org.junit.Test;
import pl.pilionerzy.model.AudienceAnswer;
import pl.pilionerzy.model.Prefix;
import pl.pilionerzy.model.Question;
import pl.pilionerzy.util.Unchecker;

import java.util.Arrays;
import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;
import static pl.pilionerzy.util.lifeline.AskTheAudienceCalculator.getAnswer;

public class AskTheAudienceCalculatorTest {

    private final Question question = new Question();

    @Before
    public void setCorrectAnswer() {
        question.setCorrectAnswer(Prefix.A);
    }

    @Test
    public void shouldContainAllAnswersWhenFiftyFiftyWasNotUsed() {
        assertThat(getAnswer(question, Collections.emptySet()))
                .containsOnlyKeys(Prefix.A, Prefix.B, Prefix.C, Prefix.D);
    }

    @Test
    public void shouldContainOnlyRemainingAnswersWhenFiftyFiftyWasUsed() {
        assertThat(getAnswer(question, Arrays.asList(Prefix.C, Prefix.D)))
                .containsOnlyKeys(Prefix.A, Prefix.B);
    }

    @Test
    public void shouldSumUpTo100PercentWhenFiftyFiftyWasNotUsed() {
        int sum = getAnswer(question, Collections.emptySet()).values().stream()
                .map(AudienceAnswer::toString)
                .map(Unchecker.uncheck(AudienceAnswer.format::parse))
                .mapToInt(Number::intValue)
                .sum();

        assertThat(sum)
                .withFailMessage("Audience answers sum up to %s%% instead of 100%%.", sum)
                .isEqualTo(100);
    }

    @Test
    public void shouldSumUpTo100PercentWhenFiftyFiftyWasUsed() {
        int sum = getAnswer(question, Arrays.asList(Prefix.C, Prefix.D)).values().stream()
                .map(AudienceAnswer::toString)
                .map(Unchecker.uncheck(AudienceAnswer.format::parse))
                .mapToInt(Number::intValue)
                .sum();

        assertThat(sum)
                .withFailMessage("Audience answers sum up to %s%% instead of 100%%.", sum)
                .isEqualTo(100);
    }
}
