package pl.pilionerzy.service;

import com.google.common.collect.Sets;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import pl.pilionerzy.exception.GameException;
import pl.pilionerzy.model.Game;
import pl.pilionerzy.model.Prefix;
import pl.pilionerzy.model.Question;

import java.util.HashSet;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.Mockito.doReturn;
import static pl.pilionerzy.model.Prefix.*;

@RunWith(MockitoJUnitRunner.class)
public class AnswerServiceTest {

    private static final long GAME_ID = 123L;
    private static final long QUESTION_ID = 456L;

    @Mock
    private GameService gameService;

    @InjectMocks
    private AnswerService answerService;

    @Test
    public void shouldThrowExceptionForInactiveGame() {
        Game inactiveGame = prepareGame(false);
        doReturn(inactiveGame).when(gameService).findById(GAME_ID);

        assertThatExceptionOfType(GameException.class)
                .isThrownBy(() -> answerService.doAnswer(GAME_ID, A))
                .withMessageContaining("inactive");
    }

    @Test
    public void shouldAcceptCorrectAnswer() {
        Game game = prepareGame(true);
        doReturn(game).when(gameService).findById(GAME_ID);

        answerService.doAnswer(GAME_ID, A);

        assertThat(game)
                .hasFieldOrPropertyWithValue("active", true)
                .hasFieldOrPropertyWithValue("level", 1);
    }

    @Test
    public void shouldNotAcceptIncorrectAnswer() {
        Game game = prepareGame(true);
        doReturn(game).when(gameService).findById(GAME_ID);

        answerService.doAnswer(GAME_ID, B);

        assertThat(game)
                .hasFieldOrPropertyWithValue("active", false)
                .hasFieldOrPropertyWithValue("level", 0);
    }

    @Test
    public void shouldDeactivateGameOnHighestLevel() {
        Game game = prepareGame(true);
        game.setLevel(11);
        game.setLastAskedQuestion(prepareQuestion(11L, D));
        game.setAskedQuestions(prepareQuestions(12));
        doReturn(game).when(gameService).findById(GAME_ID);

        answerService.doAnswer(GAME_ID, D);

        assertThat(game)
                .hasFieldOrPropertyWithValue("active", false)
                .hasFieldOrPropertyWithValue("level", 12);
    }

    @Test
    public void shouldThrowExceptionForGameWithoutLastQuestion() {
        Game game = prepareGame(true);
        game.setLastAskedQuestion(null);
        doReturn(game).when(gameService).findById(GAME_ID);

        assertThatExceptionOfType(GameException.class)
                .isThrownBy(() -> answerService.doAnswer(GAME_ID, A))
                .withMessage("Game does not have last asked question");
    }

    @Test
    public void shouldIncreaseLevelWhenAnswerIsCorrect() {
        Game game = prepareGame(true);
        game.setLevel(5);
        game.setLastAskedQuestion(prepareQuestion(5L, B));
        doReturn(game).when(gameService).findById(GAME_ID);

        answerService.doAnswer(GAME_ID, B);

        assertThat(game)
                .hasFieldOrPropertyWithValue("active", true)
                .hasFieldOrPropertyWithValue("level", 6);
    }

    @Test
    public void shouldDecreaseLevelWhenAnswerIsIncorrect() {
        Game game = prepareGame(true);
        game.setLevel(5);
        game.setLastAskedQuestion(prepareQuestion(5L, B));
        doReturn(game).when(gameService).findById(GAME_ID);

        answerService.doAnswer(GAME_ID, C);

        assertThat(game)
                .hasFieldOrPropertyWithValue("active", false)
                .hasFieldOrPropertyWithValue("level", 2);
    }

    private Game prepareGame(Boolean active) {
        Question question = prepareQuestion(QUESTION_ID, A);
        Game game = new Game();
        game.setId(GAME_ID);
        game.setActive(active);
        game.setLastAskedQuestion(question);
        game.setAskedQuestions(Sets.newHashSet(question));
        game.setLevel(0);
        return game;
    }

    private Question prepareQuestion(Long questionId, Prefix correctAnswer) {
        Question question = new Question();
        question.setId(questionId);
        question.setCorrectAnswer(correctAnswer);
        return question;
    }

    private Set<Question> prepareQuestions(int limit) {
        Set<Question> questions = new HashSet<>(limit);
        for (int i = 0; i < limit; i++) {
            questions.add(prepareQuestion((long) i, D));
        }
        return questions;
    }
}
