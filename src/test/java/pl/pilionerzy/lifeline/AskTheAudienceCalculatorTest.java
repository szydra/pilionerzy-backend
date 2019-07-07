package pl.pilionerzy.lifeline;

import com.google.common.collect.Maps;
import org.assertj.core.data.Offset;
import org.junit.Before;
import org.junit.Test;
import org.mockito.internal.util.collections.Sets;
import pl.pilionerzy.lifeline.model.AudienceAnswer;
import pl.pilionerzy.lifeline.model.PartialAudienceAnswer;
import pl.pilionerzy.model.Prefix;
import pl.pilionerzy.model.Question;

import java.util.Arrays;
import java.util.Collections;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

public class AskTheAudienceCalculatorTest {

    private final AskTheAudienceCalculator calculator = new AskTheAudienceCalculator();
    private final Question question = new Question();

    @Before
    public void setCorrectAnswer() {
        question.setCorrectAnswer(Prefix.A);
    }

    @Test
    public void shouldContainAllAnswersWhenFiftyFiftyWasNotUsed() {
        assertThat(calculator.getAnswer(question, Collections.emptySet()).getVotesChart())
                .containsOnlyKeys(Prefix.A, Prefix.B, Prefix.C, Prefix.D);
    }

    @Test
    public void shouldContainOnlyRemainingAnswersWhenFiftyFiftyWasUsed() {
        assertThat(calculator.getAnswer(question, Arrays.asList(Prefix.C, Prefix.D)).getVotesChart())
                .containsOnlyKeys(Prefix.A, Prefix.B);
    }

    @Test
    public void shouldSumUpTo100PercentWhenFiftyFiftyWasNotUsed() {
        int sum = calculator.getAnswer(question, Collections.emptySet()).getVotesChart().values().stream()
                .mapToInt(PartialAudienceAnswer::getVotes)
                .sum();

        assertThat(sum)
                .withFailMessage("Audience answers sum up to %s%% instead of 100%%.", sum)
                .isEqualTo(100);
    }

    @Test
    public void shouldSumUpTo100PercentWhenFiftyFiftyWasUsed() {
        int sum = calculator.getAnswer(question, Arrays.asList(Prefix.C, Prefix.D)).getVotesChart().values().stream()
                .mapToInt(PartialAudienceAnswer::getVotes)
                .sum();

        assertThat(sum)
                .withFailMessage("Audience answers sum up to %s%% instead of 100%%.", sum)
                .isEqualTo(100);
    }

    @Test
    public void shouldBeDistributedUniformly() {
        // Given 1000 draws for audience answer
        int numberOfDraws = 1_000;
        Map<Prefix, Double> averages = Maps.newHashMap(Maps.asMap(Sets.newSet(Prefix.values()), prefix -> 0.0));

        for (int i = 0; i < numberOfDraws; i++) {
            AudienceAnswer answer = calculator.getAnswer(question, Collections.emptySet());
            averages.replaceAll((prefix, result) -> result += answer.getVotesChart().get(prefix).getVotes());
        }

        // When calculating the averages
        averages.replaceAll((prefix, result) -> result / numberOfDraws);

        // The results should be distributed: A approx. 50% and B, C, D approx. 16.6%
        assertThat(averages.get(Prefix.A))
                .withFailMessage("Average result for correct answer is expected to be between" +
                        " 45%% and 55%%, but was %s%%.", averages.get(Prefix.A))
                .isCloseTo(50.0, Offset.offset(5.0));

        assertThat(Arrays.asList(averages.get(Prefix.B), averages.get(Prefix.C), averages.get(Prefix.D)))
                .allSatisfy(average -> assertThat(average)
                        .withFailMessage("Average result for incorrect answer is expected to be between"
                                + " 11.6%% and 21.6%%, but was %s%%.", average)
                        .isCloseTo(16.6, Offset.offset(5.0)));
    }
}
