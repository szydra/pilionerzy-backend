package pl.pilionerzy.dto;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import pl.pilionerzy.model.Prefix;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;

class QuestionDtoTest {

    @Test
    void jsonShouldBePreparedCorrectly() throws IOException {
        QuestionDto question = prepareQuestionWithFourAnswers();
        String expectedJson = "{\"content\": \"content\","
                + "\"answers\": ["
                + "  {\"prefix\": \"A\",\"content\": \"A\"},"
                + "  {\"prefix\": \"B\",\"content\": \"B\"},"
                + "  {\"prefix\": \"C\",\"content\": \"C\"},"
                + "  {\"prefix\": \"D\",\"content\": \"D\"}"
                + "]}";

        String actualJson = new ObjectMapper().writeValueAsString(question);

        assertThat(actualJson).isEqualToIgnoringWhitespace(expectedJson);
    }

    @Test
    void equalsShouldNotThrowStackOverflowError() {
        QuestionDto question1 = prepareQuestionWithOneAnswer();
        QuestionDto question2 = prepareQuestionWithOneAnswer();

        assertThatCode(() -> question1.equals(question2)).doesNotThrowAnyException();
    }

    @Test
    void hashCodeShouldNotThrowStackOverflowError() {
        QuestionDto question = prepareQuestionWithOneAnswer();

        assertThatCode(question::hashCode).doesNotThrowAnyException();
    }

    private QuestionDto prepareQuestionWithFourAnswers() {
        QuestionDto question = new QuestionDto();
        List<AnswerDto> answers = Arrays.stream(Prefix.values())
                .map(this::mapToAnswer)
                .collect(Collectors.toList());
        answers.forEach(answer -> answer.setQuestion(question));
        question.setContent("content");
        question.setAnswers(answers);
        return question;
    }

    private AnswerDto mapToAnswer(Prefix prefix) {
        AnswerDto answer = new AnswerDto();
        answer.setPrefix(prefix);
        answer.setContent(prefix.toString());
        return answer;
    }

    private QuestionDto prepareQuestionWithOneAnswer() {
        QuestionDto question = new QuestionDto();
        AnswerDto answer = new AnswerDto();
        question.setAnswers(Collections.singletonList(answer));
        answer.setQuestion(question);
        return question;
    }
}
