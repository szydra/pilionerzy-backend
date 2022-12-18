package pl.pilionerzy.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.transaction.annotation.Transactional;
import pl.pilionerzy.exception.GameException;
import pl.pilionerzy.exception.LifelineException;
import pl.pilionerzy.exception.NoSuchGameException;
import pl.pilionerzy.lifeline.LifelineProcessor;
import pl.pilionerzy.lifeline.model.FiftyFiftyResult;
import pl.pilionerzy.model.Game;
import pl.pilionerzy.model.Question;
import pl.pilionerzy.model.UsedLifeline;
import pl.pilionerzy.repository.GameRepository;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Optional;

import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.collect.Sets.newHashSet;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;
import static pl.pilionerzy.model.Lifeline.FIFTY_FIFTY;
import static pl.pilionerzy.model.Prefix.A;
import static pl.pilionerzy.model.Prefix.B;

@ExtendWith(MockitoExtension.class)
class GameServiceTest {

    @Mock
    private LifelineProcessor<?> lifelineProcessor;

    @Mock
    private GameRepository gameRepository;

    @Spy
    private List<LifelineProcessor<?>> lifelineProcessors = newArrayList();

    @InjectMocks
    private GameService gameService;

    @BeforeEach
    void initLifelineProcessors() {
        lifelineProcessors.add(lifelineProcessor);
    }

    @Test
    void stoppingGameShouldBeTransactional() throws NoSuchMethodException {
        Method stopById = GameService.class.getMethod("stopById", Long.class);

        assertThat(stopById.isAnnotationPresent(Transactional.class)).isTrue();
    }

    @Test
    void shouldThrowExceptionWhenStoppingInactiveGame() {
        // given: inactive game
        var game = newGameWithId(1L);
        game.deactivate();
        doReturn(Optional.of(game)).when(gameRepository).findByIdWithLastQuestionAndAnswers(1L);

        // when: trying to stop
        // then: exception is thrown
        assertThatExceptionOfType(GameException.class)
                .isThrownBy(() -> gameService.stopById(1L))
                .withMessage("Inactive game cannot be deactivated");
    }

    @Test
    void shouldThrowExceptionForNonExistingGame() {
        // given: non-existing game
        doReturn(Optional.empty()).when(gameRepository).findByIdWithAskedQuestions(1L);

        // when: trying to find it
        // then: exception is thrown
        assertThatExceptionOfType(NoSuchGameException.class)
                .isThrownBy(() -> gameService.findByIdWithAskedQuestions(1L));
    }

    @Test
    void shouldUpdateLastQuestion() {
        // given
        var question1 = new Question();
        question1.setId(11L);
        question1.setHash("abc");
        var question2 = new Question();
        question2.setId(12L);
        question2.setHash("def");
        var game = new Game();
        game.setId(1L);
        game.setLastAskedQuestion(question1);
        game.setAskedQuestions(newHashSet(question1));

        // when
        gameService.updateLastQuestion(game, question2);

        // then
        assertThat(game.getLastAskedQuestion())
                .isEqualTo(question2);
        assertThat(game.getAskedQuestions())
                .containsExactlyInAnyOrder(question1, question2);
    }

    // Unit tests for lifelines

    @Test
    void shouldThrowExceptionWhenLifelineIsAppliedToANonExistingGame() {
        // given: non-existing game
        doReturn(Optional.empty()).when(gameRepository).findByIdWithUsedLifelines(1L);

        // when: trying to use lifeline
        // then: exception is thrown
        assertThatExceptionOfType(NoSuchGameException.class)
                .isThrownBy(() -> gameService.processLifeline(1L, FIFTY_FIFTY));
    }

    @Test
    void shouldThrowExceptionWhenLifelineIsAppliedToAGameWithoutLastAskedQuestion() {
        // given: game without last asked question
        var game = newGameWithId(1L);
        doReturn(Optional.of(game)).when(gameRepository).findByIdWithUsedLifelines(1L);

        // when: trying to use a lifeline
        // then: exception is thrown
        assertThatExceptionOfType(GameException.class)
                .isThrownBy(() -> gameService.processLifeline(1L, FIFTY_FIFTY))
                .withMessage("Cannot use a lifeline for a game without last asked question");
    }

    @Test
    void shouldThrowExceptionWhenLifelineWasAlreadyUsed() {
        // given: game with fifty-fifty used
        var fiftyFifty = new UsedLifeline();
        fiftyFifty.setType(FIFTY_FIFTY);
        var game = newGameWithId(2L);
        game.setLastAskedQuestion(new Question());
        game.setUsedLifelines(newArrayList(fiftyFifty));
        doReturn(Optional.of(game)).when(gameRepository).findByIdWithUsedLifelines(2L);

        // when: trying to use fifty-fifty once again
        // then: exception is thrown
        assertThatExceptionOfType(LifelineException.class)
                .isThrownBy(() -> gameService.processLifeline(2L, FIFTY_FIFTY))
                .withMessage("fifty-fifty lifeline already used");
    }

    @Test
    void shouldThrowExceptionWhenLifelineIsAppliedToInactiveGame() {
        // given: inactive game
        var game = newGameWithId(3L);
        game.deactivate();
        doReturn(Optional.of(game)).when(gameRepository).findByIdWithUsedLifelines(3L);

        // when: trying to use a lifeline
        // then: exception is thrown
        assertThatExceptionOfType(GameException.class)
                .isThrownBy(() -> gameService.processLifeline(3L, FIFTY_FIFTY))
                .withMessage("Game with id 3 is inactive");
    }

    @Test
    void shouldApplyLifelineWhenNoLifelineWasUsed() {
        // given
        var game = newGameWithId(3L);
        game.setLastAskedQuestion(new Question());
        game.setUsedLifelines(newArrayList());
        doReturn(new FiftyFiftyResult(newHashSet(A, B))).when(lifelineProcessor).process(game);
        doReturn(FIFTY_FIFTY).when(lifelineProcessor).type();
        doReturn(Optional.of(game)).when(gameRepository).findByIdWithUsedLifelines(3L);

        // when
        gameService.processLifeline(3L, FIFTY_FIFTY);

        // then
        verify(lifelineProcessor).process(game);
    }

    private Game newGameWithId(long gameId) {
        var game = new Game();
        game.setId(gameId);
        game.activate();
        game.setUsedLifelines(newArrayList());
        return game;
    }
}
