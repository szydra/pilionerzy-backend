package pl.pilionerzy.controller;

import com.google.common.collect.ImmutableMap;
import org.hamcrest.Matchers;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.dao.DataAccessException;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import pl.pilionerzy.dto.NewQuestionDto;
import pl.pilionerzy.exception.GameException;
import pl.pilionerzy.exception.LifelineException;
import pl.pilionerzy.exception.NoSuchGameException;
import pl.pilionerzy.model.Prefix;

import javax.validation.ConstraintViolationException;

import static org.mockito.Answers.RETURNS_SMART_NULLS;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@WebMvcTest(controllers = ExceptionHandler.class)
public class ExceptionHandlerTest {

    @Autowired
    private MockMvc mvc;

    @MockBean
    private GameController gameController;

    @MockBean
    private QuestionController questionController;

    @Test
    public void shouldHandleConstraintViolation() throws Exception {
        ConstraintViolationException exception = mock(ConstraintViolationException.class, RETURNS_SMART_NULLS);
        when(questionController.add(isA(NewQuestionDto.class))).thenThrow(exception);

        mvc.perform(post("/questions")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"content\": \"content\","
                        + "\"correctAnswer\":\"A\","
                        + "\"answers\": ["
                        + "  {\"prefix\": \"A\",\"content\": \"A\"},"
                        + "  {\"prefix\": \"B\",\"content\": \"B\"},"
                        + "  {\"prefix\": \"C\",\"content\": \"C\"},"
                        + "  {\"prefix\": \"D\",\"content\": \"D\"}"
                        + "]}"))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(Matchers.startsWith("Constraint violations")));
        verify(questionController).add(isA(NewQuestionDto.class));
    }

    @Test
    public void shouldHandleMethodArgumentInvalid() throws Exception {
        mvc.perform(post("/questions")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{}"))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(Matchers.startsWith("Validation errors")));
    }

    @Test
    public void shouldHandleDataAccessException() throws Exception {
        when(gameController.createNewGame()).thenThrow(mock(DataAccessException.class));

        mvc.perform(get("/games/start-new"))
                .andExpect(status().isServiceUnavailable());
        verify(gameController).createNewGame();
    }

    @Test
    public void shouldHandleGameException() throws Exception {
        when(gameController.sendAnswer(anyLong(), anyMap())).thenThrow(GameException.class);

        mvc.perform(post("/games/1/answers")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"selected\":\"A\"}"))
                .andExpect(status().isBadRequest());
        verify(gameController).sendAnswer(1L, ImmutableMap.of("selected", Prefix.A));
    }

    @Test
    public void shouldHandleLifelineException() throws Exception {
        when(gameController.getAudienceAnswer(anyLong())).thenThrow(LifelineException.class);

        mvc.perform(get("/games/1/ask-the-audience"))
                .andExpect(status().isForbidden());
        verify(gameController).getAudienceAnswer(1L);
    }

    @Test
    public void shouldHandleNoSuchGameException() throws Exception {
        when(gameController.sendAnswer(anyLong(), anyMap())).thenThrow(NoSuchGameException.class);

        mvc.perform(post("/games/1/answers")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"selected\":\"A\"}"))
                .andExpect(status().isNotFound());
        verify(gameController).sendAnswer(1L, ImmutableMap.of("selected", Prefix.A));
    }
}
