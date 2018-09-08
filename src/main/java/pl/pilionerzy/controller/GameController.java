package pl.pilionerzy.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import pl.pilionerzy.model.Game;
import pl.pilionerzy.service.GameService;

import java.util.Collections;
import java.util.Map;

@CrossOrigin(origins = "${allowed.origins}")
@RestController
@RequestMapping("/game")
public class GameController {

    private GameService gameService;

    @Autowired
    public GameController(GameService gameService) {
        this.gameService = gameService;
    }

    @GetMapping("/start")
    public Game createNewGame() {
        return gameService.startNewGame();
    }

    @GetMapping("/stop/{gameId}")
    public Map<String, String> stopGame(@PathVariable Long gameId) {
        Game game = gameService.stopGame(gameId);
        return Collections.singletonMap("prefix", String.valueOf(gameService.getCorrectAnswerPrefix(game)));
    }

}
