package pl.pilionerzy.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import pl.pilionerzy.dto.NewQuestionDto;
import pl.pilionerzy.mapping.DtoMapper;
import pl.pilionerzy.model.Question;
import pl.pilionerzy.service.QuestionService;

@CrossOrigin(origins = "${allowed.origins}")
@RestController
@RequestMapping("/questions")
public class QuestionController {

    private DtoMapper dtoMapper;
    private QuestionService questionService;

    @Autowired
    public QuestionController(DtoMapper dtoMapper, QuestionService questionService) {
        this.dtoMapper = dtoMapper;
        this.questionService = questionService;
    }

    @GetMapping(params = "game-id")
    public Question getByGameId(@RequestParam("game-id") Long gameId) {
        return questionService.getNextQuestion(gameId);
    }

    @PostMapping
    public void add(@RequestBody NewQuestionDto question) {
        questionService.save(dtoMapper.mapToModel(question));
    }

}
