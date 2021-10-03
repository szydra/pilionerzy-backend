package pl.pilionerzy.controller;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import pl.pilionerzy.dto.GameDto;
import pl.pilionerzy.dto.QuestionDto;
import pl.pilionerzy.lifeline.model.AudienceAnswer;
import pl.pilionerzy.lifeline.model.FiftyFiftyResult;
import pl.pilionerzy.lifeline.model.FriendsAnswer;
import pl.pilionerzy.lifeline.model.PartialAudienceAnswer;
import pl.pilionerzy.service.AnswerService;
import pl.pilionerzy.service.GameService;
import pl.pilionerzy.service.QuestionService;

import java.util.List;
import java.util.Map;

import static org.hamcrest.Matchers.contains;
import static org.mockito.Mockito.doReturn;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static pl.pilionerzy.model.Lifeline.*;
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

    @MockBean
    private QuestionService questionService;

    @Test
    public void shouldReturnNewGame() throws Exception {
        var game = new GameDto();
        game.setId(1_487L);
        doReturn(game).when(gameService).startNewGame();

        mvc.perform(get("/games/start-new"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1_487L))
                .andExpect(jsonPath("$.correctAnswer").doesNotExist());
    }

    @Test
    public void shouldProcessRequest() throws Exception {
        var game = new GameDto();
        game.setCorrectAnswer(C);
        doReturn(game).when(answerService).doAnswer(50L, A);

        mvc.perform(post("/games/50/answers")
                        .contentType(APPLICATION_JSON)
                        .content("{\"selected\":\"A\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.correctAnswer").value("C"));
    }

    @Test
    public void shouldReturnStoppedGameWithCorrectAnswer() throws Exception {
        var game = new GameDto();
        game.setCorrectAnswer(D);

        doReturn(game).when(gameService).stopById(907L);

        mvc.perform(post("/games/907/stop")
                        .contentType(APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.correctAnswer").value("D"));
    }

    @Test
    public void shouldProcessFiftyFiftyLifeline() throws Exception {
        doReturn(new FiftyFiftyResult(List.of(A, B))).when(gameService).processLifeline(25L, FIFTY_FIFTY);

        mvc.perform(get("/games/25/fifty-fifty"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.incorrectPrefixes").value(contains("A", "B")));
    }

    @Test
    public void shouldProcessPhoneAFriendLifeline() throws Exception {
        doReturn(new FriendsAnswer(C, 80)).when(gameService).processLifeline(26L, PHONE_A_FRIEND);

        mvc.perform(get("/games/26/phone-a-friend"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.prefix").value("C"))
                .andExpect(jsonPath("$.wisdom").value("80%"));
    }

    @Test
    public void shouldProcessAskTheAudienceLifeline() throws Exception {
        doReturn(new AudienceAnswer(Map.of(
                A, PartialAudienceAnswer.withVotes(10),
                B, PartialAudienceAnswer.withVotes(20),
                C, PartialAudienceAnswer.withVotes(30),
                D, PartialAudienceAnswer.withVotes(40)))
        ).when(gameService).processLifeline(27L, ASK_THE_AUDIENCE);

        mvc.perform(get("/games/27/ask-the-audience"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.votesChart.A").value("10%"))
                .andExpect(jsonPath("$.votesChart.B").value("20%"))
                .andExpect(jsonPath("$.votesChart.C").value("30%"))
                .andExpect(jsonPath("$.votesChart.D").value("40%"));
    }

    @Test
    public void shouldResponseWithCorrectJson() throws Exception {
        QuestionDto question = new QuestionDto();
        question.setContent("What is bad?");
        doReturn(question).when(questionService).getNextQuestionByGameId(1L);

        mvc.perform(get("/games/1/questions"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").value("What is bad?"))
                .andExpect(jsonPath("$.correctAnswer").doesNotExist());
    }
}
