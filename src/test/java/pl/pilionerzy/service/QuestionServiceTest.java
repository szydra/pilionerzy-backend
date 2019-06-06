package pl.pilionerzy.service;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import pl.pilionerzy.dao.QuestionDao;
import pl.pilionerzy.exception.NotEnoughDataException;
import pl.pilionerzy.model.Game;
import pl.pilionerzy.model.Question;

import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.*;
import static pl.pilionerzy.service.QuestionService.LIMIT;

@RunWith(MockitoJUnitRunner.class)
public class QuestionServiceTest {

    private final long gameId = 123L;

    @Mock
    private QuestionDao questionDao;

    @Mock
    private GameService gameService;

    @Mock
    private Page<Question> page;

    @InjectMocks
    private QuestionService questionService;

    private Game game;
    private Question question;

    @Before
    public void init() {
        prepareGameAndQuestion();
        prepareMocks();
    }

    private void prepareGameAndQuestion() {
        question = new Question();
        question.setId(1L);
        game = new Game();
        game.setId(gameId);
        game.setLevel(1);
        game.setActive(true);
        game.setAskedQuestions(Collections.singleton(question));
    }

    private void prepareMocks() {
        doReturn(game).when(gameService).findById(gameId);
        doReturn(page).when(questionDao).findByActive(isA(Boolean.class), isA(Pageable.class));
        doReturn(1L).when(questionDao).countByActive(true);
    }

    @Test
    public void shouldSaveQuestion() {
        questionService.save(question);

        verify(questionDao).save(question);
    }

    @Test
    public void shouldUpdateLastQuestion() {
        Question otherQuestion = new Question();
        otherQuestion.setId(2L);
        doReturn(true).when(page).hasContent();
        doReturn(Collections.singletonList(otherQuestion)).when(page).getContent();

        Question actualQuestion = questionService.getNextQuestionByGameId(gameId);

        verify(gameService)
                .updateLastQuestion(game, otherQuestion);
        assertThat(actualQuestion)
                .isEqualTo(otherQuestion);
    }

    @Test
    public void shouldThrowExceptionWhenThereAreNoQuestions() {
        doReturn(false).when(page).hasContent();

        assertThatExceptionOfType(NotEnoughDataException.class)
                .isThrownBy(() -> questionService.getNextQuestionByGameId(gameId));
        verify(questionDao)
                .findByActive(eq(true), isA(PageRequest.class));
    }

    @Test
    public void shouldThrowExceptionWhenThereAreNotEnoughQuestions() {
        doReturn(true).when(page).hasContent();
        doReturn(Collections.singletonList(question)).when(page).getContent();

        assertThatExceptionOfType(NotEnoughDataException.class)
                .isThrownBy(() -> questionService.getNextQuestionByGameId(gameId));
        verify(questionDao, times(LIMIT))
                .findByActive(eq(true), isA(PageRequest.class));
    }

}
