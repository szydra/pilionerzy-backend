package pl.pilionerzy.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import pl.pilionerzy.model.Question;
import pl.pilionerzy.service.QuestionService;

@RestController
@RequestMapping("/question")
public class QuestionController {

    private QuestionService questionService;

    @Autowired
    public QuestionController(QuestionService questionService) {
        this.questionService = questionService;
    }

    @GetMapping("/get/{gameId}")
    public Question getByGameId(@PathVariable Long gameId) {
        return questionService.getNextQuestion(gameId);
    }

    @PostMapping("/add")
    public void add(@RequestBody Question question) {
        questionService.save(question);
    }
}
