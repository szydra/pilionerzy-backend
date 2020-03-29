package pl.pilionerzy.service;

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
import pl.pilionerzy.lifeline.model.FiftyFiftyResult;
import pl.pilionerzy.lifeline.model.FriendsAnswer;
import pl.pilionerzy.lifeline.model.PartialAudienceAnswer;
import pl.pilionerzy.model.*;

import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Map;
import java.util.Optional;

import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.collect.Sets.newHashSet;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.Mockito.doReturn;
import static pl.pilionerzy.model.Lifeline.*;
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
        Game game = mockNewGameWithId(1L);
        game.deactivate();

        assertThatExceptionOfType(GameException.class)
                .isThrownBy(() -> gameService.stopById(1L))
                .withMessage("Inactive game cannot be deactivated");
    }

    @Test
    public void shouldThrowExceptionForNonExistingGame() {
        doReturn(Optional.empty()).when(gameDao).findById(1L);

        assertThatExceptionOfType(NoSuchGameException.class)
                .isThrownBy(() -> gameService.findByIdWithAskedQuestions(1L));
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

    // Unit tests for fifty-fifty lifeline

    @Test
    public void shouldThrowExceptionWhenFiftyFiftyIsAppliedToANonExistingGame() {
        doReturn(Optional.empty()).when(gameDao).findById(1L);

        assertThatExceptionOfType(NoSuchGameException.class)
                .isThrownBy(() -> gameService.getTwoIncorrectPrefixes(1L));
    }

    @Test
    public void shouldThrowExceptionWhenFiftyFiftyIsAppliedToAGameWithoutLastAskedQuestion() {
        mockNewGameWithId(1L);

        assertThatExceptionOfType(GameException.class)
                .isThrownBy(() -> gameService.getTwoIncorrectPrefixes(1L))
                .withMessage("Cannot use a lifeline for a game without last asked question");
    }

    @Test
    public void shouldThrowExceptionWhenFiftyFiftyWasAlreadyUsed() {
        UsedLifeline fiftyFifty = new UsedLifeline();
        fiftyFifty.setType(FIFTY_FIFTY);
        Game game = mockNewGameWithId(2L);
        game.setLastAskedQuestion(new Question());
        game.setUsedLifelines(newArrayList(fiftyFifty));

        assertThatExceptionOfType(LifelineException.class)
                .isThrownBy(() -> gameService.getTwoIncorrectPrefixes(2L))
                .withMessage("Fifty-fifty lifeline already used");
    }

    @Test
    public void shouldThrowExceptionWhenFiftyIsAppliedToInactiveGame() {
        Game game = mockNewGameWithId(3L);
        game.deactivate();

        assertThatExceptionOfType(GameException.class)
                .isThrownBy(() -> gameService.getTwoIncorrectPrefixes(3L))
                .withMessage("Game with id 3 is inactive");
    }

    @Test
    public void shouldApplyFiftyWhenNoLifelineWasUsed() {
        // given
        Game game = mockNewGameWithId(3L);
        Question question = new Question();
        game.setLastAskedQuestion(question);
        game.setUsedLifelines(newArrayList());
        doReturn(new FiftyFiftyResult(newHashSet(A, B))).when(calculator).getFiftyFiftyResult(question);

        // when
        Collection<Prefix> incorrectPrefixes = gameService.getTwoIncorrectPrefixes(3L);

        // then
        assertThat(incorrectPrefixes)
                .containsExactlyInAnyOrder(A, B);
        assertThat(game.getUsedLifelines())
                .hasSize(1)
                .allMatch(lifeline -> lifeline.getType() == FIFTY_FIFTY)
                .allSatisfy(usedLifeline ->
                        assertThat(usedLifeline.getRejectedAnswers())
                                .hasSameElementsAs(incorrectPrefixes));
    }

    // Unit tests for phone-a-friend lifeline

    @Test
    public void shouldThrowExceptionWhenPhoneAFriendIsAppliedToANonExistingGame() {
        doReturn(Optional.empty()).when(gameDao).findById(4L);

        assertThatExceptionOfType(NoSuchGameException.class)
                .isThrownBy(() -> gameService.getFriendsAnswerByGameId(4L));
    }

    @Test
    public void shouldThrowExceptionWhenPhoneAFriendIsAppliedToAGameWithoutLastAskedQuestion() {
        mockNewGameWithId(5L);

        assertThatExceptionOfType(GameException.class)
                .isThrownBy(() -> gameService.getFriendsAnswerByGameId(5L))
                .withMessage("Cannot use a lifeline for a game without last asked question");
    }

    @Test
    public void shouldThrowExceptionWhenPhoneAFriendWasAlreadyUsed() {
        UsedLifeline phoneAFriend = new UsedLifeline();
        phoneAFriend.setType(PHONE_A_FRIEND);
        Game game = mockNewGameWithId(6L);
        game.setLastAskedQuestion(new Question());
        game.setUsedLifelines(newArrayList(phoneAFriend));

        assertThatExceptionOfType(LifelineException.class)
                .isThrownBy(() -> gameService.getFriendsAnswerByGameId(6L))
                .withMessage("Phone a friend lifeline already used");
    }

    @Test
    public void shouldThrowExceptionWhenPhoneAFriendIsAppliedToInactiveGame() {
        Game game = mockNewGameWithId(7L);
        game.deactivate();

        assertThatExceptionOfType(GameException.class)
                .isThrownBy(() -> gameService.getFriendsAnswerByGameId(7L))
                .withMessage("Game with id 7 is inactive");
    }

    @Test
    public void shouldApplyPhoneAFriendWhenNoLifelineWasUsed() {
        // given
        Game game = mockNewGameWithId(8L);
        Question question = new Question();
        game.setLastAskedQuestion(question);
        game.setUsedLifelines(newArrayList());
        doReturn(new FriendsAnswer(A, 50)).when(calculator).getFriendsAnswer(question, newHashSet());

        // when
        FriendsAnswer friendsAnswer = gameService.getFriendsAnswerByGameId(8L);

        // then
        assertThat(friendsAnswer.getPrefix()).isEqualTo(A);
        assertThat(friendsAnswer.getWisdom()).isEqualTo("50%");
        assertThat(game.getUsedLifelines())
                .hasSize(1)
                .allMatch(lifeline -> lifeline.getType() == PHONE_A_FRIEND);
    }

    // Unit tests for ask-the-audience lifeline

    @Test
    public void shouldThrowExceptionWhenAskTheAudienceIsAppliedToANonExistingGame() {
        doReturn(Optional.empty()).when(gameDao).findById(1L);

        assertThatExceptionOfType(NoSuchGameException.class)
                .isThrownBy(() -> gameService.getAudienceAnswerByGameId(1L));
    }

    @Test
    public void shouldThrowExceptionWhenAskTheAudienceIsAppliedToAGameWithoutLastAskedQuestion() {
        mockNewGameWithId(1L);

        assertThatExceptionOfType(GameException.class)
                .isThrownBy(() -> gameService.getAudienceAnswerByGameId(1L));
    }

    @Test
    public void shouldThrowExceptionWhenAskTheAudienceWasAlreadyUsed() {
        UsedLifeline ata = new UsedLifeline();
        ata.setType(ASK_THE_AUDIENCE);
        Game game = mockNewGameWithId(1L);
        game.setLastAskedQuestion(new Question());
        game.setUsedLifelines(newArrayList(ata));

        assertThatExceptionOfType(LifelineException.class)
                .isThrownBy(() -> gameService.getAudienceAnswerByGameId(1L))
                .withMessageContaining("Ask the audience lifeline already used");
    }

    @Test
    public void shouldThrowExceptionWhenAskTheAudienceIsAppliedToInactiveGame() {
        Game game = mockNewGameWithId(1L);
        game.deactivate();

        assertThatExceptionOfType(GameException.class)
                .isThrownBy(() -> gameService.getAudienceAnswerByGameId(1L))
                .withMessageContaining("Game with id 1 is inactive");
    }

    @Test
    public void shouldApplyAskTheAudienceWhenFiftyFiftyWasUsed() {
        // given
        Question question = new Question();
        question.setId(1L);
        Answer answer = new Answer();
        answer.setPrefix(C);
        answer.setCorrect(true);

        UsedLifeline fiftyFifty = new UsedLifeline();
        fiftyFifty.setType(FIFTY_FIFTY);
        fiftyFifty.setQuestion(question);
        fiftyFifty.setRejectedAnswers(newHashSet(A, B));

        Game game = new Game();
        game.setLastAskedQuestion(question);
        game.setUsedLifelines(newArrayList(fiftyFifty));

        doReturn(Optional.of(game)).when(gameDao).findByIdWithUsedLifelines(1L);
        doReturn(new AudienceAnswer(Map.of(
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
        Answer answer = new Answer();
        answer.setPrefix(C);
        answer.setCorrect(true);

        Game game = new Game();
        game.setLastAskedQuestion(question);
        game.setUsedLifelines(newArrayList());

        doReturn(Optional.of(game)).when(gameDao).findByIdWithUsedLifelines(1L);
        doReturn(new AudienceAnswer(Map.of(
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

    private Game mockNewGameWithId(long gameId) {
        Game game = new Game();
        game.setId(gameId);
        game.activate();
        game.setUsedLifelines(newArrayList());
        doReturn(Optional.of(game)).when(gameDao).findById(gameId);
        doReturn(Optional.of(game)).when(gameDao).findByIdWithLastQuestionAndAnswers(gameId);
        doReturn(Optional.of(game)).when(gameDao).findByIdWithUsedLifelines(gameId);
        return game;
    }
}
