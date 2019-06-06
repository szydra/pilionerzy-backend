package pl.pilionerzy.controller;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import pl.pilionerzy.model.Question;
import pl.pilionerzy.service.QuestionService;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@WebMvcTest(controllers = QuestionController.class)
@ComponentScan("pl.pilionerzy.mapping")
public class QuestionControllerTest {

    @Autowired
    private MockMvc mvc;

    @MockBean
    private QuestionService questionService;

    @Test
    public void shouldRejectJsonWithoutCorrectAnswer() throws Exception {
        String jsonWithoutCorrectAnswer = "{\"content\": \"content\","
                + "\"answers\": ["
                + "  {\"prefix\": \"A\",\"content\": \"A\"},"
                + "  {\"prefix\": \"B\",\"content\": \"B\"},"
                + "  {\"prefix\": \"C\",\"content\": \"\"},"
                + "  {\"prefix\": \"D\",\"content\": \"D\"}"
                + "]}";

        mvc.perform(post("/questions")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonWithoutCorrectAnswer))
                .andExpect(status().isBadRequest());
        verifyZeroInteractions(questionService);
    }

    @Test
    public void shouldRejectJsonWithInvalidChild() throws Exception {
        String jsonWithInvalidChild = "{\"content\": \"content\","
                + "\"correctAnswer\":\"A\","
                + "\"answers\": ["
                + "  {\"prefix\": \"A\",\"content\": \"A\"},"
                + "  {\"prefix\": \"B\",\"content\": \"B\"},"
                + "  {\"prefix\": \"C\",\"content\": \"\"},"
                + "  {\"prefix\": \"D\",\"content\": \"D\"}"
                + "]}";

        mvc.perform(post("/questions")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonWithInvalidChild))
                .andExpect(status().isBadRequest());
        verifyZeroInteractions(questionService);
    }

    @Test
    public void shouldAcceptValidJson() throws Exception {
        String validJson = "{\"content\": \"content\","
                + "\"correctAnswer\":\"A\","
                + "\"answers\": ["
                + "  {\"prefix\": \"A\",\"content\": \"A\"},"
                + "  {\"prefix\": \"B\",\"content\": \"B\"},"
                + "  {\"prefix\": \"C\",\"content\": \"C\"},"
                + "  {\"prefix\": \"D\",\"content\": \"D\"}"
                + "]}";

        mvc.perform(post("/questions")
                .contentType(MediaType.APPLICATION_JSON)
                .content(validJson))
                .andExpect(status().isOk());
        verify(questionService).save(isA(Question.class));
    }

    @Test
    public void shouldResponseWithCorrectJson() throws Exception {
        Question question = new Question();
        question.setContent("What is bad?");
        doReturn(question).when(questionService).getNextQuestionByGameId(1L);

        String responseContent = mvc.perform(get("/questions")
                .param("game-id", "1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        assertThat(responseContent)
                .contains("\"content\":\"What is bad?\"")
                .doesNotContain("correctAnswer");
    }

}
