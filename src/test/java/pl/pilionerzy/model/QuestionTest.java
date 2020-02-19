package pl.pilionerzy.model;

import org.junit.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThatIllegalStateException;
import static pl.pilionerzy.assertion.Assertions.assertThat;

public class QuestionTest {

    private Question question = new Question();

    @Test
    public void shouldActivateInactiveQuestion() {
        question.setActive(false);

        question.activate();

        assertThat(question).isActive();
    }

    @Test
    public void shouldThrowExceptionWhenActivatingActiveQuestion() {
        question.setActive(true);

        assertThatIllegalStateException()
                .isThrownBy(() -> question.activate())
                .withMessage("Active question cannot be activated");
    }

    @Test
    public void shouldDeactivateActiveQuestion() {
        question.setActive(true);

        question.deactivate();

        assertThat(question).isInactive();
    }

    @Test
    public void shouldThrowExceptionWhenDeactivatingInactiveQuestion() {
        question.setActive(false);

        assertThatIllegalStateException()
                .isThrownBy(() -> question.deactivate())
                .withMessage("Inactive question cannot be deactivated");
    }

    @Test
    public void questionsWithTheSameBusinessIdShouldBeEqual() {
        String businessId = "1a2b3c4c5e6f7g8h";
        question.setBusinessId(businessId);
        question.setId(1L);
        question.setContent("content 1");

        Question otherQuestion = new Question();
        otherQuestion.setBusinessId(businessId);
        otherQuestion.setId(2L);
        otherQuestion.setContent("content 2");

        assertThat(question).isEqualTo(otherQuestion);
    }

    @Test
    public void questionsWithDistinctBusinessIdsShouldNotBeEqual() {
        question.setBusinessId("1a2b3c4c5e6f7g8h");

        Question otherQuestion = new Question();
        otherQuestion.setBusinessId("8h1a2b3c4c5e6f7g");

        assertThat(question).isNotEqualTo(otherQuestion);
    }

    @Test
    public void shouldReturnCorrectAnswer() {
        Answer answer = new Answer();
        answer.setPrefix(Prefix.A);
        answer.setCorrect(true);
        answer.setQuestion(question);
        question.setAnswers(List.of(answer));

        assertThat(question.getCorrectAnswer()).isEqualTo(answer);
    }

    @Test
    public void shouldThrowExceptionWhenThereIsNoCorrectAnswer() {
        Answer answer = new Answer();
        answer.setPrefix(Prefix.A);
        answer.setCorrect(false);
        answer.setQuestion(question);
        question.setAnswers(List.of(answer));

        assertThatIllegalStateException()
                .isThrownBy(() -> question.getCorrectAnswer())
                .withMessage("Question 'null' does not have correct answer");
    }
}
