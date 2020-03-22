package pl.pilionerzy.util;

import com.google.common.collect.ImmutableSet;
import org.junit.Test;
import pl.pilionerzy.exception.GameException;
import pl.pilionerzy.model.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.google.common.collect.Sets.newHashSet;
import static org.assertj.core.api.Assertions.*;
import static pl.pilionerzy.model.Lifeline.*;
import static pl.pilionerzy.model.Prefix.*;
import static pl.pilionerzy.util.GameUtils.*;
import static pl.pilionerzy.util.RequestType.*;

public class GameUtilsTest {

    private Game game = new Game();

    private void prepareQuestions() {
        Question q1 = new Question();
        q1.setId(1L);
        q1.setBusinessId("abc");
        Answer answer1 = new Answer();
        answer1.setPrefix(A);
        answer1.setCorrect(true);
        Question q2 = new Question();
        q2.setId(2L);
        q2.setBusinessId("def");
        Answer answer2 = new Answer();
        answer2.setPrefix(B);
        answer2.setCorrect(true);
        game.setAskedQuestions(newHashSet(q1, q2));
        game.setLastAskedQuestion(q2);
    }

    private void prepareLifelines(Lifeline... lifelines) {
        List<UsedLifeline> usedLifelines = new ArrayList<>();
        Arrays.stream(lifelines)
                .forEach(lifeline -> {
                    UsedLifeline usedLifeline = new UsedLifeline();
                    usedLifeline.setType(lifeline);
                    usedLifelines.add(usedLifeline);
                });
        game.setUsedLifelines(usedLifelines);
    }

    @Test
    public void shouldThrowExceptionWhenGameIsInactive() {
        game.deactivate();

        assertThat(RequestType.values()).allSatisfy(requestType ->
                assertThatExceptionOfType(GameException.class)
                        .isThrownBy(() -> validate(game, requestType))
                        .withMessageContaining("inactive")
        );
    }

    @Test
    public void shouldThrowExceptionForTooManyRequestsForQuestion() {
        prepareQuestions();
        game.setLevel(1);

        assertThatExceptionOfType(GameException.class)
                .isThrownBy(() -> validate(game, QUESTION))
                .withMessageContaining("Invalid number of requests");
    }

    @Test
    public void shouldThrowExceptionForTooManyRequestsForAnswer() {
        prepareQuestions();
        game.setLevel(2);

        assertThatExceptionOfType(GameException.class)
                .isThrownBy(() -> validate(game, ANSWER))
                .withMessageContaining("Invalid number of requests");
    }

    @Test
    public void testValidRequestForQuestion() {
        prepareQuestions();
        game.setLevel(2);

        assertThatCode(() -> validate(game, QUESTION))
                .doesNotThrowAnyException();
    }

    @Test
    public void testUnknownRequestType() {
        game.activate();

        assertThatExceptionOfType(GameException.class)
                .isThrownBy(() -> validate(game, UNKNOWN));
    }

    @Test
    public void testValidRequestForAnswer() {
        prepareQuestions();
        game.setLevel(1);

        assertThatCode(() -> validate(game, ANSWER))
                .doesNotThrowAnyException();
    }

    @Test
    public void shouldThrowExceptionWhenLastQuestionIsNotPresent() {
        assertThatExceptionOfType(GameException.class)
                .isThrownBy(() -> validate(game, ANSWER))
                .withMessageContaining("Game does not have last asked question");
    }

    @Test
    public void shouldBePossibleToApplyLifelineToGameWithLastAskedQuestion() {
        prepareQuestions();

        assertThatCode(() -> validate(game, LIFELINE))
                .doesNotThrowAnyException();
    }

    @Test
    public void shouldNotBePossibleToApplyLifelineToGameWithLastAskedQuestion() {
        assertThatExceptionOfType(GameException.class)
                .isThrownBy(() -> validate(game, LIFELINE))
                .withMessage("Cannot use a lifeline for a game without last asked question");
    }

    @Test
    public void shouldWorkCorrectlyWhenNoLifelineWasUsed() {
        prepareQuestions();
        prepareLifelines();

        assertThat(isLifelineUsed(game, FIFTY_FIFTY)).isFalse();
    }

    @Test
    public void shouldWorkCorrectlyWhenRequestedLifelineWasUsed() {
        prepareQuestions();
        prepareLifelines(PHONE_A_FRIEND);

        assertThat(isLifelineUsed(game, PHONE_A_FRIEND)).isTrue();
    }

    @Test
    public void shouldWorkCorrectlyWhenRequestedLifelineWasNotUsed() {
        prepareQuestions();
        prepareLifelines(FIFTY_FIFTY, PHONE_A_FRIEND);

        assertThat(isLifelineUsed(game, ASK_THE_AUDIENCE)).isFalse();
    }

    @Test
    public void shouldReturnEmptySetWhenNoLifelineWasUsed() {
        prepareQuestions();
        prepareLifelines();

        assertThat(getRejectedAnswers(game)).isEmpty();
    }

    @Test
    public void shouldReturnRejectedPrefixesWhenFiftyWasUsed() {
        prepareQuestions();
        prepareLifelines(FIFTY_FIFTY);
        game.getUsedLifelines().stream()
                .filter(lifeline -> lifeline.getType() == FIFTY_FIFTY)
                .forEach(lifeline -> {
                    lifeline.setQuestion(game.getLastAskedQuestion());
                    lifeline.setRejectedAnswers(ImmutableSet.of(A, C));
                });

        assertThat(getRejectedAnswers(game)).containsExactlyInAnyOrder(A, C);
    }
}
