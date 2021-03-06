package pl.pilionerzy.controller;

import com.google.common.collect.ImmutableMap;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import pl.pilionerzy.dto.GameDto;
import pl.pilionerzy.lifeline.model.AudienceAnswer;
import pl.pilionerzy.lifeline.model.FriendsAnswer;
import pl.pilionerzy.lifeline.model.PartialAudienceAnswer;
import pl.pilionerzy.model.Prefix;
import pl.pilionerzy.service.AnswerService;
import pl.pilionerzy.service.GameService;

import static com.google.common.collect.Lists.newArrayList;
import static org.mockito.Mockito.doReturn;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static pl.pilionerzy.model.Prefix.*;

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

        mvc.perform(get("/games/start-new"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1_487L))
                .andExpect(jsonPath("$.correctAnswer").doesNotExist());
    }

    @Test
    public void shouldProcessRequest() throws Exception {
        doReturn(C).when(answerService).doAnswer(50L, A);

        mvc.perform(post("/games/50/answers")
                .contentType(APPLICATION_JSON)
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
                .contentType(APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.correctAnswer").value("D"));
    }

    @Test
    public void shouldProcessFiftyFiftyLifeline() throws Exception {
        doReturn(newArrayList(A, B)).when(gameService).getTwoIncorrectPrefixes(25L);

        mvc.perform(get("/games/25/fifty-fifty"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.incorrectPrefixes").value(newArrayList("A", "B")));
    }

    @Test
    public void shouldProcessPhoneAFriendLifeline() throws Exception {
        doReturn(new FriendsAnswer(C, 80)).when(gameService).getFriendsAnswerByGameId(26L);

        mvc.perform(get("/games/26/phone-a-friend"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.prefix").value("C"))
                .andExpect(jsonPath("$.wisdom").value("80%"));
    }

    @Test
    public void shouldProcessAskTheAudienceLifeline() throws Exception {
        doReturn(new AudienceAnswer(ImmutableMap.of(
                A, PartialAudienceAnswer.withVotes(10),
                B, PartialAudienceAnswer.withVotes(20),
                C, PartialAudienceAnswer.withVotes(30),
                D, PartialAudienceAnswer.withVotes(40)))
        ).when(gameService).getAudienceAnswerByGameId(27L);

        mvc.perform(get("/games/27/ask-the-audience"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.A").value("10%"))
                .andExpect(jsonPath("$.B").value("20%"))
                .andExpect(jsonPath("$.C").value("30%"))
                .andExpect(jsonPath("$.D").value("40%"));
    }
}
