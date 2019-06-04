package pl.pilionerzy.model;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalStateException;

public class QuestionTest {

    private Question question = new Question();

    @Test
    public void shouldActivateInactiveQuestion() {
        question.setActive(false);

        question.activate();

        assertThat(question.getActive()).isTrue();
    }

    @Test
    public void shouldThrowExceptionWhenActivatingActiveQuestion() {
        question.setActive(true);

        assertThatIllegalStateException()
                .isThrownBy(() -> question.activate())
                .withMessage("Active question cannot be activated");
    }
}
