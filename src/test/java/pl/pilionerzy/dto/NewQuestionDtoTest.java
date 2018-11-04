package pl.pilionerzy.dto;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;
import pl.pilionerzy.model.Prefix;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;

public class NewQuestionDtoTest {

    @Test
    public void jsonShouldBeReadCorrectly() throws IOException {
        String json = "{\"content\": \"content\","
                + "\"answers\": ["
                + "  {\"prefix\": \"A\",\"content\": \"A\"},"
                + "  {\"prefix\": \"B\",\"content\": \"B\"},"
                + "  {\"prefix\": \"C\",\"content\": \"C\"},"
                + "  {\"prefix\": \"D\",\"content\": \"D\"}],"
                + "\"correctAnswer\": \"C\"}";
        NewQuestionDto expectedQuestion = prepareExpectedQuestion();

        NewQuestionDto actualQuestion = new ObjectMapper().readValue(json, NewQuestionDto.class);

        assertThat(actualQuestion).isEqualTo(expectedQuestion);
        assertThat(actualQuestion.getAnswers()).allMatch(answer -> answer.getQuestion() == actualQuestion);
    }

    @Test
    public void equalsShouldNotThrowStackOverflowError() {
        NewQuestionDto question1 = prepareQuestionWithOneAnswer();
        NewQuestionDto question2 = prepareQuestionWithOneAnswer();

        assertThatCode(() -> question1.equals(question2)).doesNotThrowAnyException();
    }

    @Test
    public void hashCodeShouldNotThrowStackOverflowError() {
        NewQuestionDto question = prepareQuestionWithOneAnswer();

        assertThatCode(question::hashCode).doesNotThrowAnyException();
    }

    private NewQuestionDto prepareExpectedQuestion() {
        NewQuestionDto question = new NewQuestionDto();
        List<NewAnswerDto> answers = Arrays.stream(Prefix.values())
                .map(this::mapToAnswer)
                .peek(answer -> answer.setQuestion(question))
                .collect(Collectors.toList());
        question.setContent("content");
        question.setAnswers(answers);
        question.setCorrectAnswer(Prefix.C);
        return question;
    }

    private NewAnswerDto mapToAnswer(Prefix prefix) {
        NewAnswerDto answer = new NewAnswerDto();
        answer.setPrefix(prefix);
        answer.setContent(prefix.toString());
        return answer;
    }

    private NewQuestionDto prepareQuestionWithOneAnswer() {
        NewQuestionDto question = new NewQuestionDto();
        NewAnswerDto answer = new NewAnswerDto();
        question.setAnswers(Collections.singletonList(answer));
        answer.setQuestion(question);
        return question;
    }

}
