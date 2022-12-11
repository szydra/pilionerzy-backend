package pl.pilionerzy.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@ActiveProfiles("test")
class GameServiceIntegrationTest {

    @Autowired
    private GameService gameService;

    @Autowired
    private QuestionService questionService;

    @Test
    void shouldStartNewGame() {
        var startedGame = gameService.startNewGame();

        assertThat(startedGame)
                .hasFieldOrPropertyWithValue("level", 0)
                .hasFieldOrPropertyWithValue("active", true)
                .hasFieldOrPropertyWithValue("correctAnswer", null);
    }

    @Test
    void shouldStopExistingGame() {
        var newGame = gameService.startNewGame();

        var stoppedGame = gameService.stopById(newGame.getId());

        assertThat(stoppedGame)
                .hasFieldOrPropertyWithValue("id", newGame.getId())
                .hasFieldOrPropertyWithValue("level", 0)
                .hasFieldOrPropertyWithValue("active", false);
    }

    @Test
    void shouldStopGameAndReturnItWithCorrectAnswer() {
        // given
        var newGame = gameService.startNewGame();
        questionService.getNextQuestionByGameId(newGame.getId());
        var savedGame = gameService.findByIdWithLastQuestionAndAnswers(newGame.getId());
        var question = savedGame.getLastAskedQuestion();

        // when
        var stoppedGame = gameService.stopById(newGame.getId());

        // then
        assertThat(stoppedGame)
                .hasFieldOrPropertyWithValue("id", newGame.getId())
                .hasFieldOrPropertyWithValue("active", false)
                .hasFieldOrPropertyWithValue("correctAnswer", question.getCorrectAnswer().getPrefix());
    }
}
