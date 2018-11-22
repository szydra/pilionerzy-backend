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
import pl.pilionerzy.model.Game;
import pl.pilionerzy.model.Prefix;
import pl.pilionerzy.service.AnswerService;
import pl.pilionerzy.service.GameService;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doReturn;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@WebMvcTest(controllers = GameController.class)
@ComponentScan("pl.pilionerzy.mapping")
public class GameControllerTest {

    @Autowired
    private MockMvc mvc;

    @MockBean
    private AnswerService answerService;

    @MockBean
    private GameService gameService;

    @Test
    public void shouldReturnNewGame() throws Exception {
        Game game = new Game();
        game.setId(1_487L);
        doReturn(game).when(gameService).startNewGame();

        String responseContent = mvc.perform(get("/games/start-new")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        assertThat(responseContent)
                .contains("\"id\":1487")
                .doesNotContain("askedQuestions");
    }

    @Test
    public void shouldProcessRequest() throws Exception {
        doReturn(Prefix.C).when(answerService).processRequest(50L, Prefix.A);

        String responseContent = mvc.perform(post("/games/50/answers")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"selected\":\"A\"}"))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        assertThat(responseContent).contains("\"prefix\":\"C\"");
    }

    @Test
    public void shouldStopGameAndReturnCorrectAnswer() throws Exception {
        doReturn(Prefix.D).when(gameService).stopAndGetCorrectAnswerPrefix(907L);

        String responseContent = mvc.perform(put("/games/907/stop")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        assertThat(responseContent).contains("\"prefix\":\"D\"");
    }

}
