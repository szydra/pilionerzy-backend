package pl.pilionerzy.model;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThatIllegalStateException;
import static pl.pilionerzy.assertion.Assertions.assertThat;

class QuestionTest {

    private final Question question = new Question();

    @Test
    void shouldActivateInactiveQuestion() {
        question.setActive(false);

        question.activate();

        assertThat(question).isActive();
    }

    @Test
    void shouldThrowExceptionWhenActivatingActiveQuestion() {
        question.setActive(true);

        assertThatIllegalStateException()
                .isThrownBy(question::activate)
                .withMessage("Active question cannot be activated");
    }

    @Test
    void shouldDeactivateActiveQuestion() {
        question.setActive(true);

        question.deactivate();

        assertThat(question).isInactive();
    }

    @Test
    void shouldThrowExceptionWhenDeactivatingInactiveQuestion() {
        question.setActive(false);

        assertThatIllegalStateException()
                .isThrownBy(question::deactivate)
                .withMessage("Inactive question cannot be deactivated");
    }

    @Test
    void questionsWithTheSameHashShouldBeEqual() {
        String hash = "1a2b3c4c5e6f7g8h";
        question.setHash(hash);
        question.setId(1L);
        question.setContent("content 1");

        Question otherQuestion = new Question();
        otherQuestion.setHash(hash);
        otherQuestion.setId(2L);
        otherQuestion.setContent("content 2");

        assertThat(question).isEqualTo(otherQuestion);
    }

    @Test
    void questionsWithDistinctHashesShouldNotBeEqual() {
        question.setHash("1a2b3c4c5e6f7g8h");

        Question otherQuestion = new Question();
        otherQuestion.setHash("8h1a2b3c4c5e6f7g");

        assertThat(question).isNotEqualTo(otherQuestion);
    }

    @Test
    void shouldReturnCorrectAnswer() {
        Answer answer = new Answer();
        answer.setPrefix(Prefix.A);
        answer.setCorrect(true);
        answer.setQuestion(question);
        question.setAnswers(List.of(answer));

        assertThat(question.getCorrectAnswer()).isEqualTo(answer);
    }

    @Test
    void shouldThrowExceptionWhenThereIsNoCorrectAnswer() {
        Answer answer = new Answer();
        answer.setPrefix(Prefix.A);
        answer.setCorrect(false);
        answer.setQuestion(question);
        question.setAnswers(List.of(answer));

        assertThatIllegalStateException()
                .isThrownBy(question::getCorrectAnswer)
                .withMessage("Question 'null' does not have correct answer");
    }
}
