package pl.pilionerzy.lifeline;

import org.assertj.core.data.Offset;
import org.junit.Before;
import org.junit.Test;
import pl.pilionerzy.lifeline.model.PartialAudienceAnswer;
import pl.pilionerzy.model.Answer;
import pl.pilionerzy.model.Prefix;
import pl.pilionerzy.model.Question;

import java.util.List;
import java.util.Set;

import static com.google.common.collect.Maps.asMap;
import static com.google.common.collect.Maps.newHashMap;
import static org.assertj.core.api.Assertions.assertThat;
import static pl.pilionerzy.model.Prefix.*;

public class AskTheAudienceCalculatorTest {

    private final AskTheAudienceCalculator calculator = new AskTheAudienceCalculator();
    private final Question question = new Question();

    @Before
    public void setCorrectAnswer() {
        var answer = new Answer();
        answer.setPrefix(A);
        answer.setCorrect(true);
        question.setAnswers(List.of(answer));
    }

    @Test
    public void shouldContainAllAnswersWhenFiftyFiftyWasNotUsed() {
        var votesChart = calculator.getResult(question, Set.of()).getVotesChart();

        assertThat(votesChart).containsOnlyKeys(A, B, C, D);
    }

    @Test
    public void shouldContainOnlyRemainingAnswersWhenFiftyFiftyWasUsed() {
        var votesChart = calculator.getResult(question, Set.of(C, D)).getVotesChart();

        assertThat(votesChart).containsOnlyKeys(A, B);
    }

    @Test
    public void shouldSumUpTo100PercentWhenFiftyFiftyWasNotUsed() {
        int sum = calculator.getResult(question, Set.of()).getVotesChart().values().stream()
                .mapToInt(PartialAudienceAnswer::getVotes)
                .sum();

        assertThat(sum)
                .withFailMessage("Audience answers sum up to %s%% instead of 100%%.", sum)
                .isEqualTo(100);
    }

    @Test
    public void shouldSumUpTo100PercentWhenFiftyFiftyWasUsed() {
        int sum = calculator.getResult(question, Set.of(C, D)).getVotesChart().values().stream()
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
        var averages = newHashMap(asMap(Set.of(Prefix.values()), prefix -> 0.0));

        for (int i = 0; i < numberOfDraws; i++) {
            var answer = calculator.getResult(question, Set.of());
            averages.replaceAll((prefix, result) -> result += answer.getVotesChart().get(prefix).getVotes());
        }

        // When calculating the averages
        averages.replaceAll((prefix, result) -> result / numberOfDraws);

        // The results should be distributed: A approx. 50% and B, C, D approx. 16.6%
        assertThat(averages.get(A))
                .withFailMessage("Average result for correct answer is expected to be between" +
                        " 45%% and 55%%, but was %s%%.", averages.get(A))
                .isCloseTo(50.0, Offset.offset(5.0));

        assertThat(List.of(averages.get(B), averages.get(C), averages.get(D)))
                .allSatisfy(average -> assertThat(average)
                        .withFailMessage("Average result for incorrect answer is expected to be between"
                                + " 11.6%% and 21.6%%, but was %s%%.", average)
                        .isCloseTo(16.6, Offset.offset(5.0)));
    }
}
