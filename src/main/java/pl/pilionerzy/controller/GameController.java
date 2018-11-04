package pl.pilionerzy.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import pl.pilionerzy.dto.GameDto;
import pl.pilionerzy.mapping.DtoMapper;
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
    private DtoMapper dtoMapper;
    private GameService gameService;

    @Autowired
    public GameController(AnswerService answerService, DtoMapper dtoMapper, GameService gameService) {
        this.answerService = answerService;
        this.dtoMapper = dtoMapper;
        this.gameService = gameService;
    }

    @GetMapping("/start-new")
    public GameDto createNewGame() {
        return dtoMapper.mapToDto(gameService.startNewGame());
    }

    @PostMapping("/{gameId}/answers")
    public Map<String, Prefix> sendAnswer(@PathVariable Long gameId, @RequestBody Map<String, Prefix> answer) {
        Prefix selected = answer.get("selected");
        Prefix correct = answerService.processRequest(gameId, selected);
        return Collections.singletonMap("prefix", correct);
    }

    @PutMapping("/{gameId}/stop")
    public Map<String, Prefix> stopGame(@PathVariable Long gameId) {
        Prefix prefix = gameService.stopAndGetCorrectAnswerPrefix(gameId);
        return Collections.singletonMap("prefix", prefix);
    }

}
