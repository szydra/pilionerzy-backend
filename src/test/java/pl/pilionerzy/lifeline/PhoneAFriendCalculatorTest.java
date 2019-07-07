package pl.pilionerzy.lifeline;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Maps;
import org.assertj.core.data.Offset;
import org.junit.Before;
import org.junit.Test;
import org.mockito.internal.util.collections.Sets;
import pl.pilionerzy.lifeline.model.FriendsAnswer;
import pl.pilionerzy.model.Prefix;
import pl.pilionerzy.model.Question;

import java.util.Arrays;
import java.util.Collections;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;

public class PhoneAFriendCalculatorTest {

    private PhoneAFriendCalculator calculator = new PhoneAFriendCalculator();
    private Question question = new Question();

    @Before
    public void setCorrectAnswer() {
        question.setCorrectAnswer(Prefix.A);
    }

    @Test
    public void shouldBeDistributedUniformlyWhenFiftyFiftyWasUsed() {
        // Given 1000 draws for asking a friend
        int numberOfDraws = 1_000;
        Map<Prefix, Integer> prefixesToNumberOfAnswers = Maps.newHashMap(Maps.asMap(Sets.newSet(Prefix.values()), prefix -> 0));

        // When summing up the answers
        for (int i = 0; i < numberOfDraws; i++) {
            FriendsAnswer answer = calculator.getAnswer(question, ImmutableSet.of(Prefix.C, Prefix.D));
            Integer numberOfAnswers = prefixesToNumberOfAnswers.get(answer.getPrefix());
            prefixesToNumberOfAnswers.put(answer.getPrefix(), ++numberOfAnswers);
        }

        // The results should be distributed: A approx. 650 and B approx. 350
        assertThat(prefixesToNumberOfAnswers.get(Prefix.A))
                .withFailMessage("Number of friend's correct answers was expected to be between"
                        + " 600 and 700, but was %s.", prefixesToNumberOfAnswers.get(Prefix.A))
                .isCloseTo(650, Offset.offset(50));
        assertThat(prefixesToNumberOfAnswers.get(Prefix.B))
                .withFailMessage("Number of friend's incorrect answers was expected to be between"
                        + " 300 and 400, but was %s.", prefixesToNumberOfAnswers.get(Prefix.B))
                .isCloseTo(350, Offset.offset(50));
        assertThat(Arrays.asList(Prefix.C, Prefix.D))
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
        Map<Prefix, Integer> prefixesToNumberOfAnswers = Maps.newHashMap(Maps.asMap(Sets.newSet(Prefix.values()), prefix -> 0));

        // When summing up the answers
        for (int i = 0; i < numberOfDraws; i++) {
            FriendsAnswer answer = calculator.getAnswer(question, Collections.emptySet());
            Integer numberOfAnswers = prefixesToNumberOfAnswers.get(answer.getPrefix());
            prefixesToNumberOfAnswers.put(answer.getPrefix(), ++numberOfAnswers);
        }

        // The results should be distributed: A approx. 650 and B, C, D approx. 117
        assertThat(prefixesToNumberOfAnswers.get(Prefix.A))
                .withFailMessage("Number of friend's correct answers was expected to be between"
                        + " 600 and 700, but was %s.", prefixesToNumberOfAnswers.get(Prefix.A))
                .isCloseTo(650, Offset.offset(50));
        assertThat(Arrays.asList(Prefix.B, Prefix.C, Prefix.D))
                .allSatisfy(prefix ->
                        assertThat(prefixesToNumberOfAnswers.get(prefix))
                                .withFailMessage("Number of friend's answers '%s' was expected to be between"
                                        + " 67 and 167, but was %s.", prefix, prefixesToNumberOfAnswers.get(prefix))
                                .isCloseTo(117, Offset.offset(50))
                );
    }

    @Test
    public void shouldThrowExceptionWhenCorrectAnswerWasRejected() {
        assertThatIllegalArgumentException()
                .isThrownBy(() -> calculator.getAnswer(question, ImmutableSet.of(Prefix.A, Prefix.B)))
                .withMessage("Correct answer prefix cannot be rejected");
    }
}
