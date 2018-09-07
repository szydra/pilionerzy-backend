package pl.pilionerzy.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import pl.pilionerzy.model.Game;
import pl.pilionerzy.service.GameService;

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
    public Long createNewGame() {
        return gameService.startNewGame().getId();
    }

    @GetMapping("/stop/{gameId}")
    public Character stopGame(@PathVariable Long gameId) {
        Game game = gameService.stopGame(gameId);
        return gameService.getCorrectAnswerPrefix(game);
    }

}
