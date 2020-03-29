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
import pl.pilionerzy.dao.QuestionDao;
import pl.pilionerzy.lifeline.model.AudienceAnswer;
import pl.pilionerzy.lifeline.model.FiftyFiftyResult;
import pl.pilionerzy.lifeline.model.FriendsAnswer;
import pl.pilionerzy.lifeline.model.PartialAudienceAnswer;
import pl.pilionerzy.model.Prefix;
import pl.pilionerzy.model.Question;

import java.util.Collection;
import java.util.Collections;

import static com.google.common.collect.Iterables.getOnlyElement;
import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest
@ActiveProfiles("test")
@Transactional
public class CalculatorIntegrationTest {

    @Autowired
    private Calculator calculator;

    @Autowired
    private QuestionDao questionDao;

    @Test
    public void shouldApplyFiftyFifty() {
        Question question = getAnyQuestion();

        FiftyFiftyResult fiftyFiftyResult = calculator.getFiftyFiftyResult(question);

        assertThat(fiftyFiftyResult.getPrefixesToDiscard())
                .hasSize(2)
                .doesNotContain(question.getCorrectAnswer().getPrefix());
    }

    @Test
    public void shouldApplyPhoneAFriend() {
        Question question = getAnyQuestion();

        Collection<Prefix> rejectedAnswers = calculator.getFiftyFiftyResult(question).getPrefixesToDiscard();
        FriendsAnswer friendsAnswer = calculator.getFriendsAnswer(question, ImmutableSet.copyOf(rejectedAnswers));

        assertThat(friendsAnswer.getPrefix())
                .isNotIn(rejectedAnswers)
                .isIn(Sets.difference(Sets.newHashSet(Prefix.values()), ImmutableSet.copyOf(rejectedAnswers)));
    }

    @Test
    public void shouldAskTheAudience() {
        Question question = getAnyQuestion();

        AudienceAnswer audienceAnswer = calculator.getAudienceAnswer(question, Collections.emptySet());

        assertThat(audienceAnswer.getVotesChart()).satisfies(votesChart -> {
            assertThat(votesChart.keySet()).containsExactly(Prefix.A, Prefix.B, Prefix.C, Prefix.D);
            assertThat(votesChart.values().stream()
                    .mapToInt(PartialAudienceAnswer::getVotes)
                    .sum()
            ).isEqualTo(100);
        });
    }

    private Question getAnyQuestion() {
        return getOnlyElement(questionDao.findAll(PageRequest.of(0, 1)).getContent());
    }
}
