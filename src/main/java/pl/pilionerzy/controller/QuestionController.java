package pl.pilionerzy.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import pl.pilionerzy.dto.NewQuestionDto;
import pl.pilionerzy.dto.QuestionDto;
import pl.pilionerzy.service.QuestionService;

import javax.validation.Valid;

@CrossOrigin(origins = "${allowed.origins}")
@RestController
@RequestMapping("/questions")
@RequiredArgsConstructor
public class QuestionController {

    private final QuestionService questionService;

    @GetMapping(params = "game-id")
    public QuestionDto getByGameId(@RequestParam("game-id") Long gameId) {
        return questionService.getNextQuestionByGameId(gameId);
    }

    @PostMapping
    public NewQuestionDto add(@Valid @RequestBody NewQuestionDto question) {
        return questionService.saveNew(question);
    }
}
