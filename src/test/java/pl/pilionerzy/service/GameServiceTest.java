package pl.pilionerzy.service;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnitRunner;
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

@RunWith(MockitoJUnitRunner.class)
public class GameServiceTest {

    @Mock
    private LifelineProcessor<?> lifelineProcessor;

    @Mock
    private GameRepository gameRepository;

    @Spy
    private List<LifelineProcessor<?>> lifelineProcessors = newArrayList();

    @InjectMocks
    private GameService gameService;

    @Before
    public void initLifelineProcessors() {
        lifelineProcessors.add(lifelineProcessor);
    }

    @Test
    public void stoppingGameShouldBeTransactional() throws NoSuchMethodException {
        Method stopById = GameService.class.getMethod("stopById", Long.class);

        assertThat(stopById.isAnnotationPresent(Transactional.class)).isTrue();
    }

    @Test
    public void shouldThrowExceptionWhenStoppingInactiveGame() {
        Game game = mockNewGameWithId(1L);
        game.deactivate();

        assertThatExceptionOfType(GameException.class)
                .isThrownBy(() -> gameService.stopById(1L))
                .withMessage("Inactive game cannot be deactivated");
    }

    @Test
    public void shouldThrowExceptionForNonExistingGame() {
        doReturn(Optional.empty()).when(gameRepository).findByIdWithAskedQuestions(1L);

        assertThatExceptionOfType(NoSuchGameException.class)
                .isThrownBy(() -> gameService.findByIdWithAskedQuestions(1L));
    }

    @Test
    public void shouldUpdateLastQuestion() {
        Question question1 = new Question();
        question1.setId(11L);
        question1.setHash("abc");
        Question question2 = new Question();
        question2.setId(12L);
        question2.setHash("def");
        Game game = new Game();
        game.setId(1L);
        game.setLastAskedQuestion(question1);
        game.setAskedQuestions(newHashSet(question1));

        gameService.updateLastQuestion(game, question2);

        assertThat(game.getLastAskedQuestion())
                .isEqualTo(question2);
        assertThat(game.getAskedQuestions())
                .containsExactlyInAnyOrder(question1, question2);
    }

    // Unit tests for lifelines

    @Test
    public void shouldThrowExceptionWhenLifelineIsAppliedToANonExistingGame() {
        doReturn(Optional.empty()).when(gameRepository).findByIdWithUsedLifelines(1L);

        assertThatExceptionOfType(NoSuchGameException.class)
                .isThrownBy(() -> gameService.processLifeline(1L, FIFTY_FIFTY));
    }

    @Test
    public void shouldThrowExceptionWhenLifelineIsAppliedToAGameWithoutLastAskedQuestion() {
        mockNewGameWithId(1L);

        assertThatExceptionOfType(GameException.class)
                .isThrownBy(() -> gameService.processLifeline(1L, FIFTY_FIFTY))
                .withMessage("Cannot use a lifeline for a game without last asked question");
    }

    @Test
    public void shouldThrowExceptionWhenLifelineWasAlreadyUsed() {
        UsedLifeline fiftyFifty = new UsedLifeline();
        fiftyFifty.setType(FIFTY_FIFTY);
        Game game = mockNewGameWithId(2L);
        game.setLastAskedQuestion(new Question());
        game.setUsedLifelines(newArrayList(fiftyFifty));

        assertThatExceptionOfType(LifelineException.class)
                .isThrownBy(() -> gameService.processLifeline(2L, FIFTY_FIFTY))
                .withMessage("fifty-fifty lifeline already used");
    }

    @Test
    public void shouldThrowExceptionWhenLifelineIsAppliedToInactiveGame() {
        Game game = mockNewGameWithId(3L);
        game.deactivate();

        assertThatExceptionOfType(GameException.class)
                .isThrownBy(() -> gameService.processLifeline(3L, FIFTY_FIFTY))
                .withMessage("Game with id 3 is inactive");
    }

    @Test
    public void shouldApplyLifelineWhenNoLifelineWasUsed() {
        // given
        Game game = mockNewGameWithId(3L);
        game.setLastAskedQuestion(new Question());
        game.setUsedLifelines(newArrayList());
        doReturn(new FiftyFiftyResult(newHashSet(A, B))).when(lifelineProcessor).process(game);
        doReturn(FIFTY_FIFTY).when(lifelineProcessor).type();

        // when
        gameService.processLifeline(3L, FIFTY_FIFTY);

        // then
        verify(lifelineProcessor).process(game);
    }

    private Game mockNewGameWithId(long gameId) {
        Game game = new Game();
        game.setId(gameId);
        game.activate();
        game.setUsedLifelines(newArrayList());
        doReturn(Optional.of(game)).when(gameRepository).findByIdWithLastQuestionAndAnswers(gameId);
        doReturn(Optional.of(game)).when(gameRepository).findByIdWithUsedLifelines(gameId);
        return game;
    }
}
