package pl.pilionerzy.service;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
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
import pl.pilionerzy.model.*;

import java.lang.reflect.Method;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.Mockito.doReturn;
import static pl.pilionerzy.model.Prefix.*;

@RunWith(MockitoJUnitRunner.class)
public class GameServiceTest {

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
    public void shouldThrowExceptionForNonExistingGame() {
        doReturn(Optional.empty()).when(gameDao).findById(1L);

        assertThatExceptionOfType(NoSuchGameException.class)
                .isThrownBy(() -> gameService.findById(1L));
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
        game.setUsedLifelines(Lists.newArrayList());
        doReturn(Optional.of(game)).when(gameDao).findById(1L);

        assertThatExceptionOfType(GameException.class)
                .isThrownBy(() -> gameService.getAudienceAnswerByGameId(1L));
    }

    @Test
    public void shouldThrowExceptionWhenAskTheAudienceWasAlreadyUsed() {
        UsedLifeline ata = new UsedLifeline();
        ata.setType(Lifeline.ASK_THE_AUDIENCE);
        Game game = new Game();
        game.setUsedLifelines(Lists.newArrayList(ata));
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
        game.setUsedLifelines(Lists.newArrayList(fiftyFifty));

        doReturn(Optional.of(game)).when(gameDao).findById(1L);

        // when
        Map<Prefix, AudienceAnswer> audienceAnswer = gameService.getAudienceAnswerByGameId(1L);

        // then
        assertThat(audienceAnswer).containsOnlyKeys(C, D);
        assertThat(game.getUsedLifelines())
                .extracting(UsedLifeline::getType)
                .containsExactlyInAnyOrder(Lifeline.ASK_THE_AUDIENCE, Lifeline.FIFTY_FIFTY);
    }

    @Test
    public void shouldApplyAskTheAudienceWhenFiftyFiftyWasNotUsed() {
        // given
        Question question = new Question();
        question.setId(1L);
        question.setCorrectAnswer(C);

        Game game = new Game();
        game.setLastAskedQuestion(question);
        game.setUsedLifelines(Lists.newArrayList());

        doReturn(Optional.of(game)).when(gameDao).findById(1L);

        // when
        Map<Prefix, AudienceAnswer> audienceAnswer = gameService.getAudienceAnswerByGameId(1L);

        // then
        assertThat(audienceAnswer).containsOnlyKeys(A, B, C, D);
        assertThat(game.getUsedLifelines())
                .extracting(UsedLifeline::getType)
                .containsExactly(Lifeline.ASK_THE_AUDIENCE);
    }
}
