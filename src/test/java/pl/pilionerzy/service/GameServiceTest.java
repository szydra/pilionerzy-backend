package pl.pilionerzy.service;

import com.google.common.collect.Sets;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import pl.pilionerzy.dao.GameDao;
import pl.pilionerzy.exception.GameException;
import pl.pilionerzy.exception.LifelineException;
import pl.pilionerzy.exception.NoSuchGameException;
import pl.pilionerzy.model.*;

import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.*;
import static pl.pilionerzy.model.Prefix.*;

@RunWith(MockitoJUnitRunner.class)
public class GameServiceTest {

    @Mock
    private GameDao gameDao;

    @InjectMocks
    private GameService gameService;

    @Captor
    private ArgumentCaptor<Game> gameArgumentCaptor;

    @Before
    public void prepareGameDao() {
        doAnswer(invocation -> invocation.getArgument(0))
                .when(gameDao)
                .save(isA(Game.class));
    }

    @Test
    public void shouldStartNewGame() {
        Game game = gameService.startNewGame();

        assertThat(game)
                .hasFieldOrPropertyWithValue("level", 0)
                .hasFieldOrPropertyWithValue("active", true);
        assertThat(game.getAskedQuestions()).isNullOrEmpty();
    }

    @Test
    public void shouldStopExistingGame() {
        Game game = new Game();
        game.setId(1L);
        game.setActive(true);
        doReturn(Optional.of(game)).when(gameDao).findById(1L);

        Game game1 = gameService.findById(1L);
        game1.setActive(false);
        Game stoppedGame = gameService.save(game1);

        assertThat(stoppedGame.getActive()).isFalse();
    }

    @Test
    public void shouldThrowExceptionForNonExistingGame() {
        doReturn(Optional.empty()).when(gameDao).findById(1L);

        assertThatExceptionOfType(NoSuchGameException.class)
                .isThrownBy(() -> gameService.findById(1L));
    }

    @Test
    public void shouldStopAndReturnCorrectAnswer() {
        Question question = new Question();
        question.setId(8L);
        question.setCorrectAnswer(Prefix.C);
        Game game = new Game();
        game.setId(1L);
        game.setLastAskedQuestion(question);
        game.setAskedQuestions(Sets.newHashSet(question));
        doReturn(Optional.of(game)).when(gameDao).findById(1L);

        Prefix correct = gameService.stopAndGetCorrectAnswerPrefix(1L);

        assertThat(game.getActive()).isFalse();
        assertThat(correct).isEqualTo(Prefix.C);
    }

    @Test
    public void shouldUpdateLastQuestion() {
        Question question1 = new Question();
        question1.setId(11L);
        Question question2 = new Question();
        question2.setId(12L);
        Game game = new Game();
        game.setId(1L);
        game.setLastAskedQuestion(question1);
        game.setAskedQuestions(Sets.newHashSet(question1));

        gameService.updateLastQuestion(game, question2);

        verify(gameDao).save(gameArgumentCaptor.capture());
        Game capturedGame = gameArgumentCaptor.getValue();
        assertThat(capturedGame.getLastAskedQuestion())
                .isEqualTo(question2);
        assertThat(capturedGame.getAskedQuestions())
                .containsExactly(question1, question2);
    }

    @Test
    public void shouldThrowExceptionWhenAskTheAudienceIsAppliedToANonExistingGame() {
        doReturn(Optional.empty()).when(gameDao).findById(1L);

        assertThatExceptionOfType(NoSuchGameException.class)
                .isThrownBy(() -> gameService.getAudienceAnswerByGameId(1L));
    }

    @Test
    public void shouldThrowExceptionWhenAskTheAudienceIsAppliedToAGameWithoutLastAskedQuestion() {
        Game game = new Game();
        game.setUsedLifelines(Sets.newHashSet());
        doReturn(Optional.of(game)).when(gameDao).findById(1L);

        assertThatExceptionOfType(GameException.class)
                .isThrownBy(() -> gameService.getAudienceAnswerByGameId(1L));
    }

    @Test
    public void shouldThrowExceptionWhenAskTheAudienceWasAlreadyUsed() {
        UsedLifeline ata = new UsedLifeline();
        ata.setType(Lifeline.ASK_THE_AUDIENCE);
        Game game = new Game();
        game.setUsedLifelines(Sets.newHashSet(ata));
        doReturn(Optional.of(game)).when(gameDao).findById(1L);

        assertThatExceptionOfType(LifelineException.class)
                .isThrownBy(() -> gameService.getAudienceAnswerByGameId(1L));
    }

    @Test
    public void shouldApplyAskTheAudienceWhenFiftyFiftyWasUsed() {
        // given
        Question question = new Question();
        question.setId(1L);
        question.setCorrectAnswer(C);

        UsedLifeline fiftyFifty = new UsedLifeline();
        fiftyFifty.setType(Lifeline.FIFTY_FIFTY);
        fiftyFifty.setQuestion(question);
        fiftyFifty.setRejectedAnswers(Sets.newHashSet(A, B));

        Game game = new Game();
        game.setLastAskedQuestion(question);
        game.setUsedLifelines(Sets.newHashSet(fiftyFifty));

        doReturn(Optional.of(game)).when(gameDao).findById(1L);

        // when
        Map<Prefix, AudienceAnswer> audienceAnswer = gameService.getAudienceAnswerByGameId(1L);

        // then
        assertThat(audienceAnswer).containsOnlyKeys(C, D);
        assertThat(game.getUsedLifelines())
                .extracting(UsedLifeline::getType)
                .contains(Lifeline.ASK_THE_AUDIENCE);
    }

    @Test
    public void shouldApplyAskTheAudienceWhenFiftyFiftyWasNotUsed() {
        // given
        Question question = new Question();
        question.setId(1L);
        question.setCorrectAnswer(C);

        Game game = new Game();
        game.setLastAskedQuestion(question);
        game.setUsedLifelines(Sets.newHashSet());

        doReturn(Optional.of(game)).when(gameDao).findById(1L);

        // when
        Map<Prefix, AudienceAnswer> audienceAnswer = gameService.getAudienceAnswerByGameId(1L);

        // then
        assertThat(audienceAnswer).containsOnlyKeys(A, B, C, D);
        assertThat(game.getUsedLifelines())
                .extracting(UsedLifeline::getType)
                .contains(Lifeline.ASK_THE_AUDIENCE);
    }
}
