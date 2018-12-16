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
    private Question q1;
    private Question q2;

    @Before
    public void prepareGame() {
        game = new Game();
        q1 = new Question();
        q1.setId(1L);
        q1.setCorrectAnswer(Prefix.A);
        q2 = new Question();
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
    public void testTooManyRequestsForQuestion() {
        game.setLevel(1);

        assertThatExceptionOfType(GameException.class)
                .isThrownBy(() -> GameUtils.validate(game, RequestType.QUESTION))
                .withMessageContaining("Invalid number of requests");
        ;
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
        game.setLastAskedQuestion(q2);

        Prefix correctAnswerPrefix = GameUtils.getCorrectAnswerPrefix(game);

        assertThat(correctAnswerPrefix).isEqualTo(Prefix.B);
    }

    @Test
    public void shouldThrowExceptionWhenLastQuestionIsNotPresent() {
        assertThatExceptionOfType(GameException.class)
                .isThrownBy(() -> GameUtils.getCorrectAnswerPrefix(game))
                .withMessageContaining("Game does not have last asked question");
    }

}
