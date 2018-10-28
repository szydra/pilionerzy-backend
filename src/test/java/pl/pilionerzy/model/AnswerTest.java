package pl.pilionerzy.model;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import java.util.Collections;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class AnswerTest {

    private Answer answer;

    private ObjectMapper objectMapper = new ObjectMapper();

    @Before
    public void prepareAnswer() {
        answer = new Answer();
        Question question = new Question();
        answer.setId(456L);
        answer.setContent("sample answer content");
        answer.setPrefix('A');
        question.setId(123L);
        question.setContent("sample question content");
        question.setAnswers(Collections.singletonList(answer));
        question.setCorrectAnswer('A');
        answer.setQuestion(question);
    }

    @Test
    public void jsonAnswerShouldNotContainQuestion() throws JsonProcessingException {
        String answerJson = objectMapper.writeValueAsString(answer);
        assertFalse(answerJson.contains("question"));
    }

    // TODO: 28.10.18 Fix me!
    @Ignore
    @Test
    public void jsonAnswerShouldContainEverythingExceptQuestion() throws JsonProcessingException {
        String answerJson = objectMapper.writeValueAsString(answer);
        assertTrue(answerJson.contains("id"));
        assertTrue(answerJson.contains("content"));
        assertTrue(answerJson.contains("prefix"));
    }

}