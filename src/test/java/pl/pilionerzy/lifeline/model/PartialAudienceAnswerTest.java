package pl.pilionerzy.lifeline.model;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class PartialAudienceAnswerTest {

    @Test
    void shouldFormatOneDigitAnswer() {
        var partialAudienceAnswer = PartialAudienceAnswer.withVotes(5);

        assertThat(partialAudienceAnswer.toString()).isEqualTo("5%");
    }

    @Test
    void shouldFormatTwoDigitAnswer() {
        var partialAudienceAnswer = PartialAudienceAnswer.withVotes(25);

        assertThat(partialAudienceAnswer.toString()).isEqualTo("25%");
    }
}
