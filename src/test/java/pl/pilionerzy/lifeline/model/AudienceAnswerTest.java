package pl.pilionerzy.lifeline.model;

import com.google.common.collect.ImmutableMap;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static pl.pilionerzy.model.Prefix.*;

public class AudienceAnswerTest {

    @Test
    public void chartShouldBeSortedAccordingToPrefixes() {
        AudienceAnswer audienceAnswer = new AudienceAnswer(
                ImmutableMap.of(
                        B, PartialAudienceAnswer.withVotes(10),
                        A, PartialAudienceAnswer.withVotes(20),
                        D, PartialAudienceAnswer.withVotes(30),
                        C, PartialAudienceAnswer.withVotes(40)
                ));

        assertThat(audienceAnswer.getVotesChart().keySet())
                .containsExactly(A, B, C, D);
    }
}
