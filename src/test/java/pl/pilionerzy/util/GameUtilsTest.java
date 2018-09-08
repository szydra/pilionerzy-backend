package pl.pilionerzy.util;

import com.google.common.collect.Sets;
import org.junit.Before;
import org.junit.Test;
import pl.pilionerzy.exception.GameException;
import pl.pilionerzy.model.Game;
import pl.pilionerzy.model.Question;

public class GameUtilsTest {

    private Game game;

    @Before
    public void prepareGame() {
        game = new Game();
        Question q1 = new Question();
        q1.setId(1L);
        Question q2 = new Question();
        q2.setId(2L);
        game.setAskedQuestions(Sets.newHashSet(q1, q2));
    }

    @Test(expected = GameException.class)
    public void testInactiveGame() {
        game.setActive(false);
        GameUtils.validate(game, RequestType.QUESTION);
    }

    @Test(expected = GameException.class)
    public void testTooManyRequestsForAnswer() {
        game.setLevel(2);
        GameUtils.validate(game, RequestType.ANSWER);
    }

    @Test(expected = GameException.class)
    public void testTooManyRequestsForQuestion() {
        game.setLevel(1);
        GameUtils.validate(game, RequestType.QUESTION);
    }

    @Test(expected = GameException.class)
    public void testUnknownRequestType() {
        game.setActive(true);
        GameUtils.validate(game, RequestType.UNKNOWN);
    }

    @Test
    public void testValidRequests() {
        game.setLevel(1);
        GameUtils.validate(game, RequestType.ANSWER);
        game.setLevel(2);
        GameUtils.validate(game, RequestType.QUESTION);
    }

}