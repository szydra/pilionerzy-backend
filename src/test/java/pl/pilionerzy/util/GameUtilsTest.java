package pl.pilionerzy.util;

import com.google.common.collect.Sets;
import org.junit.Test;
import pl.pilionerzy.exception.GameException;
import pl.pilionerzy.model.Game;
import pl.pilionerzy.model.Prefix;
import pl.pilionerzy.model.Question;

import static org.assertj.core.api.Assertions.*;
import static pl.pilionerzy.util.GameUtils.validate;
import static pl.pilionerzy.util.RequestType.*;

public class GameUtilsTest {

    private Game game = new Game();

    private void prepareQuestions() {
        Question q1 = new Question();
        q1.setId(1L);
        q1.setCorrectAnswer(Prefix.A);
        Question q2 = new Question();
        q2.setId(2L);
        q2.setCorrectAnswer(Prefix.B);
        game.setAskedQuestions(Sets.newHashSet(q1, q2));
        game.setLastAskedQuestion(q2);
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
}
