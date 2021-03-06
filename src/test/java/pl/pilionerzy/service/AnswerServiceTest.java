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

import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.apache.commons.lang3.RandomStringUtils.randomAlphanumeric;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.Mockito.doReturn;
import static pl.pilionerzy.assertion.Assertions.assertThat;
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
                .withMessage("Game with id %s is inactive", GAME_ID);
    }

    @Test
    public void shouldAcceptCorrectAnswer() {
        Game game = prepareGame(true);
        doReturn(game).when(gameService).findById(GAME_ID);

        answerService.doAnswer(GAME_ID, A);

        assertThat(game)
                .isActive()
                .hasLevel(1);
    }

    @Test
    public void shouldNotAcceptIncorrectAnswer() {
        Game game = prepareGame(true);
        doReturn(game).when(gameService).findById(GAME_ID);

        answerService.doAnswer(GAME_ID, B);

        assertThat(game)
                .isInactive()
                .hasLevel(0);
    }

    @Test
    public void shouldDeactivateGameOnHighestLevel() {
        Game game = prepareGame(true);
        game.setLevel(11);
        game.setAskedQuestions(prepareQuestions(12));
        doReturn(game).when(gameService).findById(GAME_ID);

        answerService.doAnswer(GAME_ID, A);

        assertThat(game)
                .isInactive()
                .hasLevel(12);
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
        game.setAskedQuestions(prepareQuestions(6));
        doReturn(game).when(gameService).findById(GAME_ID);

        answerService.doAnswer(GAME_ID, A);

        assertThat(game)
                .isActive()
                .hasLevel(6);
    }

    @Test
    public void shouldDecreaseLevelWhenAnswerIsIncorrect() {
        Game game = prepareGame(true);
        game.setLevel(5);
        game.setAskedQuestions(prepareQuestions(6));
        doReturn(game).when(gameService).findById(GAME_ID);

        answerService.doAnswer(GAME_ID, C);

        assertThat(game)
                .isInactive()
                .hasLevel(2);
    }

    @Test
    public void shouldNotIncreaseLevelTwice() {
        Game game = prepareGame(true);
        game.setLevel(5);
        game.setAskedQuestions(prepareQuestions(6));
        doReturn(game).when(gameService).findById(GAME_ID);

        answerService.doAnswer(GAME_ID, A);

        assertThatExceptionOfType(GameException.class)
                .isThrownBy(() -> answerService.doAnswer(GAME_ID, A))
                .withMessage("Invalid number of requests for game with id %s", GAME_ID);

        assertThat(game)
                .isActive()
                .hasLevel(6);
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
        question.setBusinessId(randomAlphanumeric(32));
        question.setCorrectAnswer(correctAnswer);
        return question;
    }

    private Set<Question> prepareQuestions(int limit) {
        return IntStream.range(0, limit)
                .mapToObj(i -> prepareQuestion((long) i, D))
                .collect(Collectors.toSet());
    }
}
