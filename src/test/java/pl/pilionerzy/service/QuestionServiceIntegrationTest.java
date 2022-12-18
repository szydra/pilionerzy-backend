package pl.pilionerzy.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;
import pl.pilionerzy.dto.NewAnswerDto;
import pl.pilionerzy.dto.NewQuestionDto;
import pl.pilionerzy.model.Answer;
import pl.pilionerzy.model.Prefix;
import pl.pilionerzy.repository.QuestionRepository;

import javax.persistence.EntityManager;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static pl.pilionerzy.model.Prefix.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@ActiveProfiles("test")
class QuestionServiceIntegrationTest {

    @Autowired
    private GameService gameService;

    @Autowired
    private QuestionRepository questionRepository;

    @Autowired
    private QuestionService questionService;

    @Autowired
    private EntityManager entityManager;

    @Test
    @Transactional
    void shouldSaveNewQuestionWithoutActivation() {
        // given
        var newQuestionDto = new NewQuestionDto();
        newQuestionDto.setContent("Sample question");
        newQuestionDto.setCorrectAnswer(A);
        List<NewAnswerDto> answers = new ArrayList<>();
        Arrays.stream(Prefix.values()).forEach(prefix -> {
            NewAnswerDto answer = new NewAnswerDto();
            answer.setPrefix(prefix);
            answer.setContent(prefix.toString());
            answer.setQuestion(newQuestionDto);
            answers.add(answer);
        });
        newQuestionDto.setAnswers(answers);

        // when
        var savedQuestionDto = questionService.saveNew(newQuestionDto);

        // then
        var savedQuestion = questionRepository.findById(savedQuestionDto.getId());
        assertThat(savedQuestion).hasValueSatisfying(question -> {
            assertThat(question.getContent()).isEqualTo(newQuestionDto.getContent());
            assertThat(question.getCorrectAnswer().getPrefix()).isEqualTo(A);
            assertThat(question.getAnswers())
                    .hasSize(4)
                    .extracting(Answer::getPrefix)
                    .containsExactly(A, B, C, D);
            assertThat(question.getActive()).isFalse();
        });
    }

    @Test
    @Transactional
    void shouldSetLastQuestion() {
        // given
        var newGame = gameService.startNewGame();
        entityManager.flush();
        entityManager.clear();

        // when
        var questionDto = questionService.getNextQuestionByGameId(newGame.getId());
        entityManager.flush();
        entityManager.clear();

        // then
        var savedGame = gameService.findByIdWithAskedQuestions(newGame.getId());
        assertThat(savedGame.getLastAskedQuestion().getContent())
                .isEqualTo(questionDto.getContent());
        assertThat(savedGame.getAskedQuestions())
                .containsExactly(savedGame.getLastAskedQuestion());
    }
}
