package pl.pilionerzy.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import pl.pilionerzy.dto.NewQuestionDto;
import pl.pilionerzy.service.QuestionService;

import javax.validation.Valid;

@CrossOrigin(origins = "${allowed.origins}")
@RestController
@RequestMapping("/questions")
@RequiredArgsConstructor
public class QuestionController {

    private final QuestionService questionService;

    @PostMapping
    public NewQuestionDto add(@Valid @RequestBody NewQuestionDto question) {
        return questionService.saveNew(question);
    }
}
