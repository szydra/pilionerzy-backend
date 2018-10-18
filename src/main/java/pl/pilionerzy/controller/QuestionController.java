package pl.pilionerzy.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import pl.pilionerzy.model.Question;
import pl.pilionerzy.service.QuestionService;

@CrossOrigin(origins = "${allowed.origins}")
@RestController
@RequestMapping("/questions")
public class QuestionController {

    private QuestionService questionService;

    @Autowired
    public QuestionController(QuestionService questionService) {
        this.questionService = questionService;
    }

    @GetMapping(params = "game-id")
    public Question getByGameId(@RequestParam("game-id") Long gameId) {
        return questionService.getNextQuestion(gameId);
    }

    @PostMapping
    public void add(@RequestBody Question question) {
        // TODO: 18.10.18  Use QuestionDTO
        questionService.save(question);
    }

}