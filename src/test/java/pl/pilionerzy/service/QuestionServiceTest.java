package pl.pilionerzy.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import pl.pilionerzy.exception.NotEnoughDataException;
import pl.pilionerzy.model.Game;
import pl.pilionerzy.model.Question;
import pl.pilionerzy.repository.QuestionRepository;

import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.Mockito.*;
import static pl.pilionerzy.service.QuestionService.LIMIT;

@ExtendWith(MockitoExtension.class)
class QuestionServiceTest {

    private final long gameId = 123L;

    @Mock
    private QuestionRepository questionRepository;

    @Mock
    private GameService gameService;

    @Mock
    private Page<Question> page;

    @InjectMocks
    private QuestionService questionService;

    private Game game;
    private Question question;

    @BeforeEach
    void init() {
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
        game.setAskedQuestions(Set.of(question));
    }

    private void prepareMocks() {
        doReturn(game).when(gameService).findByIdWithAskedQuestions(gameId);
        // lenient because of testing exceptions
        lenient().doReturn(page).when(questionRepository).findByActive(isA(Boolean.class), isA(Pageable.class));
    }

    @Test
    void shouldThrowExceptionWhenQuestionWasNotFound() {
        // given: only one question that was already asked
        doReturn(1).when(questionRepository).countByActive(true);

        // when: trying to get next question
        // then: exception is thrown and was tried to find next question
        assertThatExceptionOfType(NotEnoughDataException.class)
                .isThrownBy(() -> questionService.getNextQuestionByGameId(gameId));
        verify(questionRepository)
                .findByActive(eq(true), isA(PageRequest.class));
    }

    @Test
    void shouldThrowExceptionWhenThereAreNotEnoughQuestions() {
        // given: only questions that were already asked
        doReturn(1).when(questionRepository).countByActive(true);
        doReturn(true).when(page).hasContent();
        doReturn(List.of(question)).when(page).getContent();

        // when: trying to get next question
        // then: exception is thrown and was tried to find next question assumed number of times
        assertThatExceptionOfType(NotEnoughDataException.class)
                .isThrownBy(() -> questionService.getNextQuestionByGameId(gameId));
        verify(questionRepository, times(LIMIT))
                .findByActive(eq(true), isA(PageRequest.class));
    }

    @Test
    void shouldThrowExceptionWhenThereAreNoQuestions() {
        // given: no questions
        doReturn(0).when(questionRepository).countByActive(true);

        // when: trying to get next question
        // then: exception is thrown
        assertThatExceptionOfType(NotEnoughDataException.class)
                .isThrownBy(() -> questionService.getNextQuestionByGameId(gameId))
                .withMessage("No active questions available");
    }
}
