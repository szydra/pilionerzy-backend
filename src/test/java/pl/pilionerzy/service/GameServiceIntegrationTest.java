package pl.pilionerzy.service;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import pl.pilionerzy.dto.GameDto;
import pl.pilionerzy.model.Game;
import pl.pilionerzy.model.Question;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest
@ActiveProfiles("test")
public class GameServiceIntegrationTest {

    @Autowired
    private GameService gameService;

    @Autowired
    private QuestionService questionService;

    @Test
    public void shouldStartNewGame() {
        GameDto startedGame = gameService.startNewGame();

        assertThat(startedGame)
                .hasFieldOrPropertyWithValue("level", 0)
                .hasFieldOrPropertyWithValue("active", true)
                .hasFieldOrPropertyWithValue("correctAnswer", null);
    }

    @Test
    public void shouldStopExistingGame() {
        GameDto newGame = gameService.startNewGame();

        GameDto stoppedGame = gameService.stopById(newGame.getId());

        assertThat(stoppedGame)
                .hasFieldOrPropertyWithValue("id", newGame.getId())
                .hasFieldOrPropertyWithValue("level", 0)
                .hasFieldOrPropertyWithValue("active", false);
    }

    @Test
    public void shouldStopGameAndReturnItWithCorrectAnswer() {
        // given
        GameDto newGame = gameService.startNewGame();
        questionService.getNextQuestionByGameId(newGame.getId());
        Game savedGame = gameService.findById(newGame.getId());
        Question question = savedGame.getLastAskedQuestion();

        // when
        GameDto stoppedGame = gameService.stopById(newGame.getId());

        // then
        assertThat(stoppedGame)
                .hasFieldOrPropertyWithValue("id", newGame.getId())
                .hasFieldOrPropertyWithValue("active", false)
                .hasFieldOrPropertyWithValue("correctAnswer", question.getCorrectAnswer());
    }
}
