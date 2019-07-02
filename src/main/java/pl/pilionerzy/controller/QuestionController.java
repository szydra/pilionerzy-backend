package pl.pilionerzy.controller;

import org.springframework.web.bind.annotation.*;
import pl.pilionerzy.dto.NewQuestionDto;
import pl.pilionerzy.dto.QuestionDto;
import pl.pilionerzy.mapping.DtoMapper;
import pl.pilionerzy.service.QuestionService;

import javax.validation.Valid;

@CrossOrigin(origins = "${allowed.origins}")
@RestController
@RequestMapping("/questions")
public class QuestionController {

    private DtoMapper dtoMapper;
    private QuestionService questionService;

    public QuestionController(DtoMapper dtoMapper, QuestionService questionService) {
        this.dtoMapper = dtoMapper;
        this.questionService = questionService;
    }

    @GetMapping(params = "game-id")
    public QuestionDto getByGameId(@RequestParam("game-id") Long gameId) {
        return dtoMapper.mapToDto(questionService.getNextQuestionByGameId(gameId));
    }

    @PostMapping
    public void add(@Valid @RequestBody NewQuestionDto question) {
        questionService.save(dtoMapper.mapToModel(question));
    }

}
