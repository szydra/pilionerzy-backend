package pl.pilionerzy.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import pl.pilionerzy.dto.GameDto;
import pl.pilionerzy.dto.QuestionDto;
import pl.pilionerzy.model.Lifeline;
import pl.pilionerzy.model.Prefix;
import pl.pilionerzy.service.AnswerService;
import pl.pilionerzy.service.GameService;
import pl.pilionerzy.service.QuestionService;

import java.util.Map;

@CrossOrigin(origins = "${allowed.origins}")
@RestController
@RequestMapping("/games")
@RequiredArgsConstructor
public class GameController {

    private final AnswerService answerService;
    private final GameService gameService;
    private final QuestionService questionService;

    @GetMapping("/start-new")
    public GameDto createNewGame() {
        return gameService.startNewGame();
    }

    @GetMapping("/{gameId}/questions")
    public QuestionDto getByGameId(@PathVariable Long gameId) {
        return questionService.getNextQuestionByGameId(gameId);
    }

    @GetMapping("/{gameId}/{lifeline}")
    public Object getLifelineResult(@PathVariable Long gameId, @PathVariable Lifeline lifeline) {
        switch (lifeline) {
            case ASK_THE_AUDIENCE:
                return gameService.getAudienceAnswerByGameId(gameId).getVotesChart();
            case FIFTY_FIFTY:
                return Map.of("incorrectPrefixes", gameService.getTwoIncorrectPrefixes(gameId));
            case PHONE_A_FRIEND:
                return gameService.getFriendsAnswerByGameId(gameId);
            default:
                throw new IllegalArgumentException("Unknown lifeline: " + lifeline);
        }
    }

    @PostMapping("/{gameId}/answers")
    public GameDto sendAnswer(@PathVariable Long gameId, @RequestBody Map<String, Prefix> answer) {
        return answerService.doAnswer(gameId, answer.get("selected"));
    }

    @PostMapping("/{gameId}/stop")
    public GameDto stopGame(@PathVariable Long gameId) {
        return gameService.stopById(gameId);
    }
}
