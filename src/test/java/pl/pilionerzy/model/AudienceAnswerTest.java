package pl.pilionerzy.model;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class AudienceAnswerTest {

    @Test
    public void shouldFormatOneDigitAnswer() {
        AudienceAnswer audienceAnswer = AudienceAnswer.withVotes(5);

        assertThat(audienceAnswer.toString()).isEqualTo("5%");
    }

    @Test
    public void shouldFormatTwoDigitAnswer() {
        AudienceAnswer audienceAnswer = AudienceAnswer.withVotes(25);

        assertThat(audienceAnswer.toString()).isEqualTo("25%");
    }
}
