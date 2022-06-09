package pl.pilionerzy.lifeline;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;
import pl.pilionerzy.lifeline.model.AudienceAnswer;
import pl.pilionerzy.lifeline.model.FiftyFiftyResult;
import pl.pilionerzy.lifeline.model.FriendsAnswer;
import pl.pilionerzy.lifeline.model.PartialAudienceAnswer;
import pl.pilionerzy.model.Game;
import pl.pilionerzy.model.Lifeline;
import pl.pilionerzy.model.Prefix;
import pl.pilionerzy.model.Question;
import pl.pilionerzy.repository.QuestionRepository;

import java.util.List;

import static com.google.common.collect.Iterables.getOnlyElement;
import static com.google.common.collect.Lists.newArrayList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;
import static pl.pilionerzy.model.Lifeline.*;
import static pl.pilionerzy.model.Prefix.*;

@RunWith(SpringRunner.class)
@SpringBootTest
@ActiveProfiles("test")
@Transactional
public class LifelineProcessorIntegrationTest {

    @Autowired
    private List<LifelineProcessor<?>> lifelineProcessors;

    @Autowired
    private QuestionRepository questionRepository;

    @Test
    public void shouldApplyFiftyFifty() {
        // given
        var game = getGame();

        // when
        var fiftyFiftyResult = getProcessor(FIFTY_FIFTY).process(game);

        // then
        assertThat(fiftyFiftyResult)
                .isInstanceOf(FiftyFiftyResult.class)
                .satisfies(result ->
                        assertThat(((FiftyFiftyResult) result).getIncorrectPrefixes())
                                .hasSize(2)
                                .doesNotContain(game.getLastAskedQuestion().getCorrectAnswer().getPrefix())
                );
    }

    @Test
    public void shouldApplyPhoneAFriend() {
        // given
        var game = getGame();

        // when
        var fiftyFiftyResult = getProcessor(FIFTY_FIFTY).process(game);
        var rejectedAnswers = ((FiftyFiftyResult) fiftyFiftyResult).getIncorrectPrefixes();
        var friendsAnswer = getProcessor(PHONE_A_FRIEND).process(game);

        // then
        assertThat(friendsAnswer)
                .isInstanceOf(FriendsAnswer.class)
                .satisfies(result ->
                        assertThat(((FriendsAnswer) result).getPrefix())
                                .isNotIn(rejectedAnswers)
                                .isIn(Sets.difference(Sets.newHashSet(Prefix.values()), ImmutableSet.copyOf(rejectedAnswers)))
                );
    }

    @Test
    public void shouldAskTheAudience() {
        // given
        var game = getGame();

        // when
        var audienceAnswer = getProcessor(ASK_THE_AUDIENCE).process(game);

        // then
        assertThat(audienceAnswer)
                .isInstanceOf(AudienceAnswer.class)
                .satisfies(result ->
                        assertThat(((AudienceAnswer) result).getVotesChart()).satisfies(votesChart -> {
                            assertThat(votesChart.keySet()).containsExactly(A, B, C, D);
                            assertThat(votesChart.values().stream()
                                    .mapToInt(PartialAudienceAnswer::getVotes)
                                    .sum()
                            ).isEqualTo(100);
                        })
                );
    }

    private Game getGame() {
        var game = new Game();
        game.setLastAskedQuestion(getAnyQuestion());
        game.setUsedLifelines(newArrayList());
        return game;
    }

    private Question getAnyQuestion() {
        return getOnlyElement(questionRepository.findAll(PageRequest.of(0, 1)).getContent());
    }

    private LifelineProcessor<?> getProcessor(Lifeline lifeline) {
        return lifelineProcessors.stream()
                .filter(processor -> processor.type() == lifeline)
                .findFirst()
                .orElseGet(() -> fail("Cannot find processor for lifeline: %s", lifeline));
    }
}
