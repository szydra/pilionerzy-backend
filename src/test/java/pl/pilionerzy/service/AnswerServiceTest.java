package pl.pilionerzy.service;

import com.google.common.collect.Sets;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import pl.pilionerzy.dto.GameDto;
import pl.pilionerzy.exception.GameException;
import pl.pilionerzy.mapping.DtoMapper;
import pl.pilionerzy.model.Answer;
import pl.pilionerzy.model.Game;
import pl.pilionerzy.model.Prefix;
import pl.pilionerzy.model.Question;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.apache.commons.lang3.RandomStringUtils.randomAlphanumeric;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.lenient;
import static pl.pilionerzy.assertion.Assertions.assertThat;
import static pl.pilionerzy.model.Prefix.*;

@ExtendWith(MockitoExtension.class)
class AnswerServiceTest {

    private static final long GAME_ID = 123L;
    private static final long QUESTION_ID = 456L;

    @Mock
    private DtoMapper dtoMapper;

    @Mock
    private GameService gameService;

    @Mock
    private LevelService levelService;

    @InjectMocks
    private AnswerService answerService;

    @BeforeEach
    void prepareGameMapper() {
        // lenient because of testing exceptions
        lenient().doAnswer(invocation -> mapToDto(invocation.getArgument(0)))
                .when(dtoMapper)
                .mapToDto(isA(Game.class));
    }

    private GameDto mapToDto(Game game) {
        var gameDto = new GameDto();
        gameDto.setActive(game.getActive());
        gameDto.setLevel(game.getLevel());
        return gameDto;
    }

    @Test
    void shouldThrowExceptionForInactiveGame() {
        // given: inactive game
        var inactiveGame = prepareGame(false);
        doReturn(inactiveGame).when(gameService).findByIdWithAskedQuestions(GAME_ID);

        // when: trying to answer
        // then: exception is thrown
        assertThatExceptionOfType(GameException.class)
                .isThrownBy(() -> answerService.doAnswer(GAME_ID, A))
                .withMessage("Game with id %s is inactive", GAME_ID);
    }

    @Test
    void shouldAcceptCorrectAnswer() {
        // given
        var game = prepareGame(true);
        doReturn(game).when(gameService).findByIdWithAskedQuestions(GAME_ID);

        // when
        answerService.doAnswer(GAME_ID, A);

        // then
        assertThat(game)
                .isActive()
                .hasLevel(1);
    }

    @Test
    void shouldNotAcceptIncorrectAnswer() {
        // given
        var game = prepareGame(true);
        doReturn(game).when(gameService).findByIdWithAskedQuestions(GAME_ID);
        doReturn(0).when(levelService).getGuaranteedLevel(0);

        // when
        answerService.doAnswer(GAME_ID, B);

        // then
        assertThat(game)
                .isInactive()
                .hasLevel(0);
    }

    @Test
    void shouldDeactivateGameOnHighestLevel() {
        // given
        var game = prepareGame(true);
        game.setLevel(11);
        game.setAskedQuestions(prepareQuestions(12));
        doReturn(game).when(gameService).findByIdWithAskedQuestions(GAME_ID);
        doReturn(true).when(levelService).isHighestLevel(12);

        // when
        answerService.doAnswer(GAME_ID, A);

        // then
        assertThat(game)
                .isInactive()
                .hasLevel(12);
    }

    @Test
    void shouldThrowExceptionForGameWithoutLastQuestion() {
        // given: game without last asked question
        var game = prepareGame(true);
        game.setLastAskedQuestion(null);
        doReturn(game).when(gameService).findByIdWithAskedQuestions(GAME_ID);

        // when: trying to answer
        // then: exception is thrown
        assertThatExceptionOfType(GameException.class)
                .isThrownBy(() -> answerService.doAnswer(GAME_ID, A))
                .withMessage("Game does not have last asked question");
    }

    @Test
    void shouldIncreaseLevelWhenAnswerIsCorrect() {
        // given
        var game = prepareGame(true);
        game.setLevel(5);
        game.setAskedQuestions(prepareQuestions(6));
        doReturn(game).when(gameService).findByIdWithAskedQuestions(GAME_ID);

        // when
        answerService.doAnswer(GAME_ID, A);

        // then
        assertThat(game)
                .isActive()
                .hasLevel(6);
    }

    @Test
    void shouldDecreaseLevelWhenAnswerIsIncorrect() {
        // given
        var game = prepareGame(true);
        game.setLevel(5);
        game.setAskedQuestions(prepareQuestions(6));
        doReturn(game).when(gameService).findByIdWithAskedQuestions(GAME_ID);
        doReturn(2).when(levelService).getGuaranteedLevel(5);

        // when
        answerService.doAnswer(GAME_ID, C);

        // then
        assertThat(game)
                .isInactive()
                .hasLevel(2);
    }

    @Test
    void shouldNotIncreaseLevelTwice() {
        // given
        var game = prepareGame(true);
        game.setLevel(5);
        game.setAskedQuestions(prepareQuestions(6));
        doReturn(game).when(gameService).findByIdWithAskedQuestions(GAME_ID);

        // when
        answerService.doAnswer(GAME_ID, A);

        // then
        assertThatExceptionOfType(GameException.class)
                .isThrownBy(() -> answerService.doAnswer(GAME_ID, A))
                .withMessage("Invalid number of requests for game with id %s", GAME_ID);

        assertThat(game)
                .isActive()
                .hasLevel(6);
    }

    private Game prepareGame(Boolean active) {
        var question = prepareQuestion(QUESTION_ID, A);
        var game = new Game();
        game.setId(GAME_ID);
        game.setActive(active);
        game.setLastAskedQuestion(question);
        game.setAskedQuestions(Sets.newHashSet(question));
        game.setLevel(0);
        return game;
    }

    private Question prepareQuestion(Long questionId, Prefix correctAnswer) {
        var question = new Question();
        question.setId(questionId);
        question.setHash(randomAlphanumeric(32));
        var answer = new Answer();
        answer.setPrefix(correctAnswer);
        answer.setCorrect(true);
        question.setAnswers(List.of(answer));
        return question;
    }

    private Set<Question> prepareQuestions(int limit) {
        return IntStream.range(0, limit)
                .mapToObj(i -> prepareQuestion((long) i, D))
                .collect(Collectors.toSet());
    }
}
