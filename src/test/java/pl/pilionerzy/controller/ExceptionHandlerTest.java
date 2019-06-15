package pl.pilionerzy.controller;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.dao.DataAccessException;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import pl.pilionerzy.exception.GameException;
import pl.pilionerzy.exception.LifelineException;
import pl.pilionerzy.exception.NoSuchGameException;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@WebMvcTest(controllers = ExceptionHandler.class)
public class ExceptionHandlerTest {

    @Autowired
    private MockMvc mvc;

    @MockBean
    private GameController gameController;

    @Test
    public void shouldHandleDataAccessException() throws Exception {
        when(gameController.createNewGame()).thenThrow(mock(DataAccessException.class));

        mvc.perform(get("/games/start-new"))
                .andExpect(status().isServiceUnavailable());
    }

    @Test
    public void shouldHandleGameException() throws Exception {
        when(gameController.sendAnswer(anyLong(), anyMap())).thenThrow(GameException.class);

        mvc.perform(post("/games/1/answers")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"selected\":\"A\"}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void shouldHandleLifelineException() throws Exception {
        when(gameController.getAudienceAnswer(anyLong())).thenThrow(LifelineException.class);

        mvc.perform(get("/games/1/ask-the-audience"))
                .andExpect(status().isForbidden());
    }

    @Test
    public void shouldHandleNoSuchGameException() throws Exception {
        when(gameController.sendAnswer(anyLong(), anyMap())).thenThrow(NoSuchGameException.class);

        mvc.perform(post("/games/1/answers")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"selected\":\"A\"}"))
                .andExpect(status().isNotFound());
    }
}
