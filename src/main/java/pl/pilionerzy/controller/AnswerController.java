package pl.pilionerzy.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import pl.pilionerzy.service.AnswerService;
import pl.pilionerzy.util.PrefixUtils;

import java.util.Collections;
import java.util.Map;

@CrossOrigin(origins = "${allowed.origins}")
@RestController
@RequestMapping("/answer")
public class AnswerController {

    private AnswerService answerService;

    @Autowired
    public AnswerController(AnswerService answerService) {
        this.answerService = answerService;
    }

    @PostMapping("/send/{gameId}")
    public Map<String, String> sendAnswer(@PathVariable Long gameId, @RequestBody Map<String, String> answer) {
        String rawSelected = answer.get("selected");
        PrefixUtils.validatePrefix(rawSelected);
        Character selected = rawSelected.trim().charAt(0);
        Character correct = answerService.processRequest(gameId, selected);
        return Collections.singletonMap("prefix", String.valueOf(correct));
    }

}
