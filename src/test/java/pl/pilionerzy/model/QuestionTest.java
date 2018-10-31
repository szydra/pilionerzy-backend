package pl.pilionerzy.model;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.Arrays;

import static org.junit.Assert.*;

public class QuestionTest {

    private Question question;

    private ObjectMapper objectMapper = new ObjectMapper();

    @Before
    public void prepareQuestion() {
        question = new Question();
        Answer answerA = new Answer();
        Answer answerB = new Answer();
        answerA.setId(456L);
        answerA.setContent("sample answer content");
        answerA.setPrefix(Prefix.A);
        answerB.setId(789L);
        answerB.setContent("sample answer content");
        answerB.setPrefix(Prefix.B);
        question.setId(123L);
        question.setContent("sample question content");
        question.setAnswers(Arrays.asList(answerA, answerB));
        question.setCorrectAnswer(Prefix.A);
        answerA.setQuestion(question);
        answerB.setQuestion(question);
    }

    @Test
    public void jsonQuestionShouldNotContainCorrectAnswer() throws JsonProcessingException {
        String jsonQuestion = objectMapper.writeValueAsString(question);
        assertFalse(jsonQuestion.contains("correctAnswer"));
    }

    @Test
    public void jsonQuestionShouldContainEverythingExceptCorrectAnswer() throws JsonProcessingException {
        String jsonQuestion = objectMapper.writeValueAsString(question);
        assertTrue(jsonQuestion.contains("id"));
        assertTrue(jsonQuestion.contains("content"));
        assertTrue(jsonQuestion.contains("answers"));
        assertTrue(jsonQuestion.contains("sample answer content"));
    }

    @Test
    public void stringToJsonQuestionShouldSetCorrectAnswer() throws IOException {
        String json = "{\"correctAnswer\":\"C\"}";
        Question questionParsed = objectMapper.readValue(json, Question.class);
        assertEquals(Prefix.C, questionParsed.getCorrectAnswer());
    }

    @Test
    public void testEqualsAndHashCode() {
        Question otherQuestion = new Question();
        otherQuestion.setId(123L);
        assertEquals(question, otherQuestion);
        assertEquals(question.hashCode(), otherQuestion.hashCode());
    }

}