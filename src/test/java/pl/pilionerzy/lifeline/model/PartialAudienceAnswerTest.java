package pl.pilionerzy.lifeline.model;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class PartialAudienceAnswerTest {

    @Test
    public void shouldFormatOneDigitAnswer() {
        PartialAudienceAnswer partialAudienceAnswer = PartialAudienceAnswer.withVotes(5);

        assertThat(partialAudienceAnswer.toString()).isEqualTo("5%");
    }

    @Test
    public void shouldFormatTwoDigitAnswer() {
        PartialAudienceAnswer partialAudienceAnswer = PartialAudienceAnswer.withVotes(25);

        assertThat(partialAudienceAnswer.toString()).isEqualTo("25%");
    }
}
