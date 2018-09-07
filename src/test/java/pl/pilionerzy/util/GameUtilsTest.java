package pl.pilionerzy.util;

import com.google.common.collect.Sets;
import org.junit.Test;
import pl.pilionerzy.exception.GameException;
import pl.pilionerzy.model.Game;
import pl.pilionerzy.model.Question;

public class GameUtilsTest {

    @Test(expected = GameException.class)
    public void testInactiveGame() {
        Game game = new Game();
        game.setActive(false);
        GameUtils.validate(game);
    }

    @Test(expected = GameException.class)
    public void testTooManyRequests() {
        Game game = new Game();
        Question q1 = new Question();
        q1.setId(1L);
        Question q2 = new Question();
        q2.setId(2L);
        game.setAskedQuestions(Sets.newHashSet(q1, q2));
        game.setLevel(1);
        GameUtils.validate(game);
    }

}