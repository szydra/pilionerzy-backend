package pl.pilionerzy.controller;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import pl.pilionerzy.dto.GameDto;
import pl.pilionerzy.model.Prefix;
import pl.pilionerzy.service.AnswerService;
import pl.pilionerzy.service.GameService;

import static org.mockito.Mockito.doReturn;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@WebMvcTest(controllers = GameController.class)
public class GameControllerTest {

    @Autowired
    private MockMvc mvc;

    @MockBean
    private AnswerService answerService;

    @MockBean
    private GameService gameService;

    @Test
    public void shouldReturnNewGame() throws Exception {
        GameDto game = new GameDto();
        game.setId(1_487L);
        doReturn(game).when(gameService).startNewGame();

        mvc.perform(get("/games/start-new")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1_487L))
                .andExpect(jsonPath("$.correctAnswer").doesNotExist());
    }

    @Test
    public void shouldProcessRequest() throws Exception {
        doReturn(Prefix.C).when(answerService).doAnswer(50L, Prefix.A);

        mvc.perform(post("/games/50/answers")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"selected\":\"A\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.prefix").value("C"));
    }

    @Test
    public void shouldReturnStoppedGameWithCorrectAnswer() throws Exception {
        GameDto game = new GameDto();
        game.setCorrectAnswer(Prefix.D);

        doReturn(game).when(gameService).stopById(907L);

        mvc.perform(post("/games/907/stop")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.correctAnswer").value("D"));
    }
}
