package pl.pilionerzy.service;

import com.google.common.collect.Sets;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
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
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class AnswerServiceTest {

    private static final long GAME_ID = 123L;
    private static final long QUESTION_ID = 456L;

    @Mock
    private GameService gameService;

    @InjectMocks
    private AnswerService answerService;

    @Captor
    private ArgumentCaptor<Game> gameArgumentCaptor;

    @Test
    public void testRequestForInactiveGame() {
        Game inactiveGame = prepareGame(false);
        doReturn(inactiveGame).when(gameService).findById(GAME_ID);

        assertThatExceptionOfType(GameException.class)
                .isThrownBy(() -> answerService.processRequest(GAME_ID, Prefix.A))
                .withMessageContaining("inactive");
    }

    @Test
    public void testRequestWithCorrectAnswer() {
        Game game = prepareGame(true);
        doReturn(game).when(gameService).findById(GAME_ID);

        answerService.processRequest(GAME_ID, Prefix.A);

        verify(gameService).save(gameArgumentCaptor.capture());
        Game capturedGame = gameArgumentCaptor.getValue();
        assertThat(capturedGame)
                .hasFieldOrPropertyWithValue("active", true)
                .hasFieldOrPropertyWithValue("level", 1);
    }

    @Test
    public void testRequestWithIncorrectAnswer() {
        Game game = prepareGame(true);
        doReturn(game).when(gameService).findById(GAME_ID);

        answerService.processRequest(GAME_ID, Prefix.B);

        verify(gameService).save(gameArgumentCaptor.capture());
        Game capturedGame = gameArgumentCaptor.getValue();
        assertThat(capturedGame)
                .hasFieldOrPropertyWithValue("active", false)
                .hasFieldOrPropertyWithValue("level", 0);
    }

    @Test
    public void testHighestAward() {
        Game game = prepareGame(true);
        game.setLevel(11);
        game.setLastAskedQuestionId(11L);
        game.setAskedQuestions(prepare12Questions());
        doReturn(game).when(gameService).findById(GAME_ID);

        answerService.processRequest(GAME_ID, Prefix.D);

        verify(gameService).save(gameArgumentCaptor.capture());
        Game capturedGame = gameArgumentCaptor.getValue();
        assertThat(capturedGame)
                .hasFieldOrPropertyWithValue("active", false)
                .hasFieldOrPropertyWithValue("level", 12);
    }

    private Game prepareGame(Boolean active) {
        Question question = prepareQuestion(QUESTION_ID, Prefix.A);
        Game game = new Game();
        game.setId(GAME_ID);
        game.setActive(active);
        game.setLastAskedQuestionId(QUESTION_ID);
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

    private Set<Question> prepare12Questions() {
        Set<Question> questions = new HashSet<>(12);
        for (int i = 0; i < 12; i++) {
            Question question = prepareQuestion((long) i, Prefix.D);
            questions.add(question);
        }
        return questions;
    }

}
