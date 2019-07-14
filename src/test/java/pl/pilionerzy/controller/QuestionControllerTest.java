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
import pl.pilionerzy.dto.NewQuestionDto;
import pl.pilionerzy.dto.QuestionDto;
import pl.pilionerzy.service.QuestionService;

import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
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
        verify(questionService).saveNew(isA(NewQuestionDto.class));
    }

    @Test
    public void shouldResponseWithCorrectJson() throws Exception {
        QuestionDto question = new QuestionDto();
        question.setContent("What is bad?");
        doReturn(question).when(questionService).getNextQuestionByGameId(1L);

        mvc.perform(get("/questions")
                .param("game-id", "1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").value("What is bad?"))
                .andExpect(jsonPath("$.correctAnswer").doesNotExist());
    }
}
