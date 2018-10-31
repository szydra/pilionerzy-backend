package pl.pilionerzy.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import pl.pilionerzy.model.Game;
import pl.pilionerzy.model.Prefix;
import pl.pilionerzy.service.AnswerService;
import pl.pilionerzy.service.GameService;

import java.util.Collections;
import java.util.Map;

@CrossOrigin(origins = "${allowed.origins}")
@RestController
@RequestMapping("/games")
public class GameController {

    private AnswerService answerService;
    private GameService gameService;

    @Autowired
    public GameController(AnswerService answerService, GameService gameService) {
        this.answerService = answerService;
        this.gameService = gameService;
    }

    @GetMapping("/start-new")
    public Game createNewGame() {
        return gameService.startNewGame();
    }

    @PostMapping("/{gameId}/answers")
    public Map<String, Prefix> sendAnswer(@PathVariable Long gameId, @RequestBody Map<String, Prefix> answer) {
        Prefix selected = answer.get("selected");
        Prefix correct = answerService.processRequest(gameId, selected);
        return Collections.singletonMap("prefix", correct);
    }

    @PutMapping("/{gameId}/stop")
    public Map<String, Prefix> stopGame(@PathVariable Long gameId) {
        Game game = gameService.stopGame(gameId);
        return Collections.singletonMap("prefix", gameService.getCorrectAnswerPrefix(game));
    }

}
