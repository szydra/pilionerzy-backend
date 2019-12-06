package pl.pilionerzy.service;

import com.google.common.collect.ImmutableMap;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.transaction.annotation.Transactional;
import pl.pilionerzy.dao.GameDao;
import pl.pilionerzy.exception.GameException;
import pl.pilionerzy.exception.LifelineException;
import pl.pilionerzy.exception.NoSuchGameException;
import pl.pilionerzy.lifeline.Calculator;
import pl.pilionerzy.lifeline.model.AudienceAnswer;
import pl.pilionerzy.lifeline.model.PartialAudienceAnswer;
import pl.pilionerzy.model.Game;
import pl.pilionerzy.model.Prefix;
import pl.pilionerzy.model.Question;
import pl.pilionerzy.model.UsedLifeline;

import java.lang.reflect.Method;
import java.util.Map;
import java.util.Optional;

import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.collect.Sets.newHashSet;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.Mockito.doReturn;
import static pl.pilionerzy.model.Lifeline.ASK_THE_AUDIENCE;
import static pl.pilionerzy.model.Lifeline.FIFTY_FIFTY;
import static pl.pilionerzy.model.Prefix.*;

@RunWith(MockitoJUnitRunner.class)
public class GameServiceTest {

    @Mock
    private Calculator calculator;

    @Mock
    private GameDao gameDao;

    @InjectMocks
    private GameService gameService;

    @Test
    public void stoppingGameShouldBeTransactional() throws NoSuchMethodException {
        Method stopById = GameService.class.getMethod("stopById", Long.class);

        assertThat(stopById.isAnnotationPresent(Transactional.class)).isTrue();
    }

    @Test
    public void shouldThrowExceptionWhenStoppingInactiveGame() {
        Game game = new Game();
        game.setId(1L);
        game.deactivate();
        doReturn(Optional.of(game)).when(gameDao).findById(1L);

        assertThatExceptionOfType(GameException.class)
                .isThrownBy(() -> gameService.stopById(1L))
                .withMessage("Inactive game cannot be deactivated");
    }

    @Test
    public void shouldThrowExceptionForNonExistingGame() {
        doReturn(Optional.empty()).when(gameDao).findById(1L);

        assertThatExceptionOfType(NoSuchGameException.class)
                .isThrownBy(() -> gameService.findById(1L));
    }

    @Test
    public void shouldUpdateLastQuestion() {
        Question question1 = new Question();
        question1.setId(11L);
        question1.setBusinessId("abc");
        Question question2 = new Question();
        question2.setId(12L);
        question2.setBusinessId("def");
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

    @Test
    public void shouldThrowExceptionWhenAskTheAudienceIsAppliedToANonExistingGame() {
        doReturn(Optional.empty()).when(gameDao).findById(1L);

        assertThatExceptionOfType(NoSuchGameException.class)
                .isThrownBy(() -> gameService.getAudienceAnswerByGameId(1L));
    }

    @Test
    public void shouldThrowExceptionWhenAskTheAudienceIsAppliedToAGameWithoutLastAskedQuestion() {
        Game game = new Game();
        game.setUsedLifelines(newArrayList());
        doReturn(Optional.of(game)).when(gameDao).findById(1L);

        assertThatExceptionOfType(GameException.class)
                .isThrownBy(() -> gameService.getAudienceAnswerByGameId(1L));
    }

    @Test
    public void shouldThrowExceptionWhenAskTheAudienceWasAlreadyUsed() {
        UsedLifeline ata = new UsedLifeline();
        ata.setType(ASK_THE_AUDIENCE);
        Game game = new Game();
        game.activate();
        game.setLastAskedQuestion(new Question());
        game.setUsedLifelines(newArrayList(ata));
        doReturn(Optional.of(game)).when(gameDao).findById(1L);

        assertThatExceptionOfType(LifelineException.class)
                .isThrownBy(() -> gameService.getAudienceAnswerByGameId(1L))
                .withMessageContaining("already used");
    }

    @Test
    public void shouldThrowExceptionWhenAskTheAudienceIsAppliedToInactiveGame() {
        Game game = new Game();
        game.deactivate();
        doReturn(Optional.of(game)).when(gameDao).findById(1L);

        assertThatExceptionOfType(GameException.class)
                .isThrownBy(() -> gameService.getAudienceAnswerByGameId(1L))
                .withMessageContaining("inactive");
    }

    @Test
    public void shouldApplyAskTheAudienceWhenFiftyFiftyWasUsed() {
        // given
        Question question = new Question();
        question.setId(1L);
        question.setCorrectAnswer(C);

        UsedLifeline fiftyFifty = new UsedLifeline();
        fiftyFifty.setType(FIFTY_FIFTY);
        fiftyFifty.setQuestion(question);
        fiftyFifty.setRejectedAnswers(newHashSet(A, B));

        Game game = new Game();
        game.setLastAskedQuestion(question);
        game.setUsedLifelines(newArrayList(fiftyFifty));

        doReturn(Optional.of(game)).when(gameDao).findById(1L);
        doReturn(new AudienceAnswer(ImmutableMap.of(
                C, PartialAudienceAnswer.withVotes(50),
                D, PartialAudienceAnswer.withVotes(50))
        )).when(calculator).getAudienceAnswer(question, newHashSet(A, B));

        // when
        Map<Prefix, PartialAudienceAnswer> audienceAnswer = gameService.getAudienceAnswerByGameId(1L).getVotesChart();

        // then
        assertThat(audienceAnswer).containsOnlyKeys(C, D);
        assertThat(game.getUsedLifelines())
                .extracting(UsedLifeline::getType)
                .containsExactlyInAnyOrder(ASK_THE_AUDIENCE, FIFTY_FIFTY);
    }

    @Test
    public void shouldApplyAskTheAudienceWhenFiftyFiftyWasNotUsed() {
        // given
        Question question = new Question();
        question.setId(1L);
        question.setCorrectAnswer(C);

        Game game = new Game();
        game.setLastAskedQuestion(question);
        game.setUsedLifelines(newArrayList());

        doReturn(Optional.of(game)).when(gameDao).findById(1L);
        doReturn(new AudienceAnswer(ImmutableMap.of(
                A, PartialAudienceAnswer.withVotes(25),
                B, PartialAudienceAnswer.withVotes(25),
                C, PartialAudienceAnswer.withVotes(25),
                D, PartialAudienceAnswer.withVotes(25))
        )).when(calculator).getAudienceAnswer(question, newHashSet());

        // when
        Map<Prefix, PartialAudienceAnswer> audienceAnswer = gameService.getAudienceAnswerByGameId(1L).getVotesChart();

        // then
        assertThat(audienceAnswer).containsOnlyKeys(A, B, C, D);
        assertThat(game.getUsedLifelines())
                .extracting(UsedLifeline::getType)
                .containsExactly(ASK_THE_AUDIENCE);
    }
}
