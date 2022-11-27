package pl.pilionerzy.controller;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import pl.pilionerzy.dto.NewQuestionDto;
import pl.pilionerzy.service.QuestionService;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@WebMvcTest(controllers = QuestionController.class)
@ComponentScan("pl.pilionerzy.mapping")
class QuestionControllerTest {

    @Autowired
    private MockMvc mvc;

    @MockBean
    private QuestionService questionService;

    @Test
    void shouldRejectJsonWithoutCorrectAnswer() throws Exception {
        var jsonWithoutCorrectAnswer = "{\"content\": \"content\","
                + "\"answers\": ["
                + "  {\"prefix\": \"A\",\"content\": \"A\"},"
                + "  {\"prefix\": \"B\",\"content\": \"B\"},"
                + "  {\"prefix\": \"C\",\"content\": \"C\"},"
                + "  {\"prefix\": \"D\",\"content\": \"D\"}"
                + "]}";

        mvc.perform(post("/questions")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonWithoutCorrectAnswer))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Validation errors: question must have correct answer"));
        verifyNoInteractions(questionService);
    }

    @Test
    void shouldRejectJsonWithInvalidChild() throws Exception {
        var jsonWithInvalidChild = "{\"content\": \"content\","
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
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Validation errors: answer content length must be between 1 and 1023"));
        verifyNoInteractions(questionService);
    }

    @Test
    void shouldAcceptValidJson() throws Exception {
        var validJson = "{\"content\": \"content\","
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
}
