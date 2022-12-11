package pl.pilionerzy.controller;

import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.dao.DataAccessException;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import pl.pilionerzy.dto.NewQuestionDto;
import pl.pilionerzy.exception.GameException;
import pl.pilionerzy.exception.LifelineException;
import pl.pilionerzy.exception.NoSuchGameException;
import pl.pilionerzy.model.Lifeline;
import pl.pilionerzy.model.Prefix;

import javax.validation.ConstraintViolationException;
import java.util.Map;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static pl.pilionerzy.model.Lifeline.ASK_THE_AUDIENCE;

@ExtendWith(SpringExtension.class)
@WebMvcTest(controllers = MainExceptionHandler.class)
class MainExceptionHandlerTest {

    @Autowired
    private MockMvc mvc;

    @MockBean
    private GameController gameController;

    @MockBean
    private QuestionController questionController;

    @Test
    void shouldHandleConstraintViolation() throws Exception {
        var exception = mock(ConstraintViolationException.class);
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
    void shouldHandleMethodArgumentInvalid() throws Exception {
        mvc.perform(post("/questions")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{}"))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(Matchers.startsWith("Validation errors")));
    }

    @Test
    void shouldHandleDataAccessException() throws Exception {
        when(gameController.createNewGame()).thenThrow(mock(DataAccessException.class));

        mvc.perform(get("/games/start-new"))
                .andExpect(status().isServiceUnavailable());
        verify(gameController).createNewGame();
    }

    @Test
    void shouldHandleGameException() throws Exception {
        when(gameController.sendAnswer(anyLong(), anyMap())).thenThrow(GameException.class);

        mvc.perform(post("/games/1/answers")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"selected\":\"A\"}"))
                .andExpect(status().isBadRequest());
        verify(gameController).sendAnswer(1L, Map.of("selected", Prefix.A));
    }

    @Test
    void shouldHandleLifelineException() throws Exception {
        when(gameController.getLifelineResult(anyLong(), isA(Lifeline.class))).thenThrow(LifelineException.class);

        mvc.perform(get("/games/1/ask-the-audience"))
                .andExpect(status().isForbidden());
        verify(gameController).getLifelineResult(1L, ASK_THE_AUDIENCE);
    }

    @Test
    void shouldHandleNoSuchGameException() throws Exception {
        when(gameController.sendAnswer(anyLong(), anyMap())).thenThrow(NoSuchGameException.class);

        mvc.perform(post("/games/1/answers")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"selected\":\"A\"}"))
                .andExpect(status().isNotFound());
        verify(gameController).sendAnswer(1L, Map.of("selected", Prefix.A));
    }
}
