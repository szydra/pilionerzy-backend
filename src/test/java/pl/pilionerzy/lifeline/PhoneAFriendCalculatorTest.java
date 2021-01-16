package pl.pilionerzy.lifeline;

import org.junit.Before;
import org.junit.Test;
import pl.pilionerzy.lifeline.model.FriendsAnswer;
import pl.pilionerzy.model.Answer;
import pl.pilionerzy.model.Prefix;
import pl.pilionerzy.model.Question;

import java.util.List;
import java.util.Map;
import java.util.Set;

import static com.google.common.collect.Maps.asMap;
import static com.google.common.collect.Maps.newHashMap;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;
import static org.assertj.core.data.Offset.offset;
import static pl.pilionerzy.model.Prefix.*;

public class PhoneAFriendCalculatorTest {

    private final PhoneAFriendCalculator calculator = new PhoneAFriendCalculator();
    private final Question question = new Question();

    @Before
    public void setCorrectAnswer() {
        Answer answer = new Answer();
        answer.setPrefix(A);
        answer.setCorrect(true);
        question.setAnswers(List.of(answer));
    }

    @Test
    public void shouldBeDistributedUniformlyWhenFiftyFiftyWasUsed() {
        // Given 1000 draws for asking a friend
        int numberOfDraws = 1_000;
        Map<Prefix, Integer> prefixesToNumberOfAnswers = newHashMap(asMap(Set.of(Prefix.values()), prefix -> 0));

        // When summing up the answers
        for (int i = 0; i < numberOfDraws; i++) {
            FriendsAnswer answer = calculator.getAnswer(question, Set.of(C, D));
            Integer numberOfAnswers = prefixesToNumberOfAnswers.get(answer.getPrefix());
            prefixesToNumberOfAnswers.put(answer.getPrefix(), ++numberOfAnswers);
        }

        // The results should be distributed: A approx. 650 and B approx. 350
        assertThat(prefixesToNumberOfAnswers.get(A))
                .withFailMessage("Number of friend's correct answers was expected to be between"
                        + " 600 and 700, but was %s.", prefixesToNumberOfAnswers.get(A))
                .isCloseTo(650, offset(50));

        assertThat(prefixesToNumberOfAnswers.get(B))
                .withFailMessage("Number of friend's incorrect answers was expected to be between"
                        + " 300 and 400, but was %s.", prefixesToNumberOfAnswers.get(B))
                .isCloseTo(350, offset(50));

        assertThat(List.of(C, D))
                .allSatisfy(prefix ->
                        assertThat(prefixesToNumberOfAnswers.get(prefix))
                                .withFailMessage("Friend was expected not to give a rejected answer.")
                                .isZero()
                );
    }

    @Test
    public void shouldBeDistributedUniformlyWhenFiftyFiftyWasNotUsed() {
        // Given 1000 draws for asking a friend
        int numberOfDraws = 1_000;
        var prefixesToNumberOfAnswers = newHashMap(asMap(Set.of(Prefix.values()), prefix -> 0));

        // When summing up the answers
        for (int i = 0; i < numberOfDraws; i++) {
            FriendsAnswer answer = calculator.getAnswer(question, Set.of());
            Integer numberOfAnswers = prefixesToNumberOfAnswers.get(answer.getPrefix());
            prefixesToNumberOfAnswers.put(answer.getPrefix(), ++numberOfAnswers);
        }

        // The results should be distributed: A approx. 650 and B, C, D approx. 117
        assertThat(prefixesToNumberOfAnswers.get(A))
                .withFailMessage("Number of friend's correct answers was expected to be between"
                        + " 600 and 700, but was %s.", prefixesToNumberOfAnswers.get(A))
                .isCloseTo(650, offset(50));
        assertThat(List.of(B, C, D))
                .allSatisfy(prefix ->
                        assertThat(prefixesToNumberOfAnswers.get(prefix))
                                .withFailMessage("Number of friend's answers '%s' was expected to be between"
                                        + " 67 and 167, but was %s.", prefix, prefixesToNumberOfAnswers.get(prefix))
                                .isCloseTo(117, offset(50))
                );
    }

    @Test
    public void shouldThrowExceptionWhenCorrectAnswerWasRejected() {
        assertThatIllegalArgumentException()
                .isThrownBy(() -> calculator.getAnswer(question, Set.of(A, B)))
                .withMessage("Correct answer prefix cannot be rejected");
    }
}
