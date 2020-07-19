package pl.pilionerzy.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import pl.pilionerzy.dto.GameDto;
import pl.pilionerzy.lifeline.model.FriendsAnswer;
import pl.pilionerzy.lifeline.model.PartialAudienceAnswer;
import pl.pilionerzy.model.Prefix;
import pl.pilionerzy.service.AnswerService;
import pl.pilionerzy.service.GameService;

import java.util.Collection;
import java.util.Map;

import static java.util.Collections.singletonMap;

@CrossOrigin(origins = "${allowed.origins}")
@RestController
@RequestMapping("/games")
@RequiredArgsConstructor
public class GameController {

    private final AnswerService answerService;
    private final GameService gameService;

    @GetMapping("/start-new")
    public GameDto createNewGame() {
        return gameService.startNewGame();
    }

    @GetMapping("/{gameId}/fifty-fifty")
    public Map<String, Collection<Prefix>> getTwoIncorrectPrefixes(@PathVariable Long gameId) {
        Collection<Prefix> incorrectPrefixes = gameService.getTwoIncorrectPrefixes(gameId);
        return singletonMap("incorrectPrefixes", incorrectPrefixes);
    }

    @GetMapping("/{gameId}/phone-a-friend")
    public FriendsAnswer getFriendsAnswer(@PathVariable Long gameId) {
        return gameService.getFriendsAnswerByGameId(gameId);
    }

    @GetMapping("/{gameId}/ask-the-audience")
    public Map<Prefix, PartialAudienceAnswer> getAudienceAnswer(@PathVariable Long gameId) {
        return gameService.getAudienceAnswerByGameId(gameId).getVotesChart();
    }

    @PostMapping("/{gameId}/answers")
    public GameDto sendAnswer(@PathVariable Long gameId, @RequestBody Map<String, Prefix> answer) {
        Prefix selected = answer.get("selected");
        return answerService.doAnswer(gameId, selected);
    }

    @PostMapping("/{gameId}/stop")
    public GameDto stopGame(@PathVariable Long gameId) {
        return gameService.stopById(gameId);
    }
}
