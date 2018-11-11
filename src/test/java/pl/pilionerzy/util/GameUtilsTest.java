package pl.pilionerzy.util;

import com.google.common.collect.Sets;
import org.junit.Before;
import org.junit.Test;
import pl.pilionerzy.exception.GameException;
import pl.pilionerzy.model.Game;
import pl.pilionerzy.model.Prefix;
import pl.pilionerzy.model.Question;

import static org.assertj.core.api.Assertions.*;

public class GameUtilsTest {

    private Game game;

    @Before
    public void prepareGame() {
        game = new Game();
        Question q1 = new Question();
        q1.setId(1L);
        q1.setCorrectAnswer(Prefix.A);
        Question q2 = new Question();
        q2.setId(2L);
        q2.setCorrectAnswer(Prefix.B);
        game.setAskedQuestions(Sets.newHashSet(q1, q2));
    }

    @Test
    public void testInactiveGame() {
        game.setActive(false);

        assertThatExceptionOfType(GameException.class)
                .isThrownBy(() -> GameUtils.validate(game, RequestType.QUESTION))
                .withMessageContaining("inactive");

    }

    @Test
    public void testTooManyRequestsForAnswer() {
        game.setLevel(2);

        assertThatExceptionOfType(GameException.class)
                .isThrownBy(() -> GameUtils.validate(game, RequestType.ANSWER));
    }

    @Test
    public void testTooManyRequestsForQuestion() {
        game.setLevel(1);

        assertThatExceptionOfType(GameException.class)
                .isThrownBy(() -> GameUtils.validate(game, RequestType.QUESTION));
    }

    @Test
    public void testUnknownRequestType() {
        game.setActive(true);

        assertThatExceptionOfType(GameException.class)
                .isThrownBy(() -> GameUtils.validate(game, RequestType.UNKNOWN));
    }

    @Test
    public void testValidRequestForQuestion() {
        game.setLevel(1);

        assertThatCode(() -> GameUtils.validate(game, RequestType.ANSWER))
                .doesNotThrowAnyException();
    }

    @Test
    public void testValidRequestForAnswer() {
        game.setLevel(2);

        assertThatCode(() -> GameUtils.validate(game, RequestType.QUESTION))
                .doesNotThrowAnyException();
    }

    @Test
    public void shouldFindCorrectAnswer() {
        game.setLastAskedQuestionId(2L);

        Prefix correctAnswerPrefix = GameUtils.getCorrectAnswerPrefix(game);

        assertThat(correctAnswerPrefix).isEqualTo(Prefix.B);
    }

    @Test
    public void shouldThrowExceptionWhenLastQuestionIsNotPresent() {
        game.setLastAskedQuestionId(3L);

        assertThatExceptionOfType(GameException.class)
                .isThrownBy(() -> GameUtils.getCorrectAnswerPrefix(game));
    }

}
