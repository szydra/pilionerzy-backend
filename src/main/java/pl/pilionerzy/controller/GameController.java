package pl.pilionerzy.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import pl.pilionerzy.dto.GameDto;
import pl.pilionerzy.dto.QuestionDto;
import pl.pilionerzy.lifeline.model.FriendsAnswer;
import pl.pilionerzy.lifeline.model.PartialAudienceAnswer;
import pl.pilionerzy.model.Prefix;
import pl.pilionerzy.service.AnswerService;
import pl.pilionerzy.service.GameService;
import pl.pilionerzy.service.QuestionService;

import java.util.Collection;
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

    @GetMapping("/{gameId}/fifty-fifty")
    public Map<String, Collection<Prefix>> getTwoIncorrectPrefixes(@PathVariable Long gameId) {
        return Map.of("incorrectPrefixes", gameService.getTwoIncorrectPrefixes(gameId));
    }

    @GetMapping("/{gameId}/phone-a-friend")
    public FriendsAnswer getFriendsAnswer(@PathVariable Long gameId) {
        return gameService.getFriendsAnswerByGameId(gameId);
    }

    @GetMapping("/{gameId}/ask-the-audience")
    public Map<Prefix, PartialAudienceAnswer> getAudienceAnswer(@PathVariable Long gameId) {
        return gameService.getAudienceAnswerByGameId(gameId).getVotesChart();
    }

    @GetMapping("/{gameId}/questions")
    public QuestionDto getByGameId(@PathVariable Long gameId) {
        return questionService.getNextQuestionByGameId(gameId);
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
