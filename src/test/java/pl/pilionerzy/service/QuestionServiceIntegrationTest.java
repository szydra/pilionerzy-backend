package pl.pilionerzy.service;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;
import pl.pilionerzy.dao.QuestionDao;
import pl.pilionerzy.dto.GameDto;
import pl.pilionerzy.dto.NewAnswerDto;
import pl.pilionerzy.dto.NewQuestionDto;
import pl.pilionerzy.dto.QuestionDto;
import pl.pilionerzy.model.Answer;
import pl.pilionerzy.model.Game;
import pl.pilionerzy.model.Prefix;
import pl.pilionerzy.model.Question;

import javax.persistence.EntityManager;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static pl.pilionerzy.model.Prefix.*;

@RunWith(SpringRunner.class)
@SpringBootTest
@ActiveProfiles("test")
public class QuestionServiceIntegrationTest {

    @Autowired
    private GameService gameService;

    @Autowired
    private QuestionDao questionDao;

    @Autowired
    private QuestionService questionService;

    @Autowired
    private EntityManager entityManager;

    @Test
    @Transactional
    public void shouldSaveNewQuestionWithoutActivation() {
        // given
        NewQuestionDto newQuestionDto = new NewQuestionDto();
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
        NewQuestionDto savedQuestionDto = questionService.saveNew(newQuestionDto);

        // then
        Optional<Question> savedQuestion = questionDao.findById(savedQuestionDto.getId());
        assertThat(savedQuestion).hasValueSatisfying(question -> {
            assertThat(question).isEqualToComparingOnlyGivenFields(newQuestionDto, "content");
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
    public void shouldSetLastQuestion() {
        // given
        GameDto newGame = gameService.startNewGame();
        entityManager.flush();
        entityManager.clear();

        // when
        QuestionDto questionDto = questionService.getNextQuestionByGameId(newGame.getId());
        entityManager.flush();
        entityManager.clear();

        // then
        Game savedGame = gameService.findByIdWithAskedQuestions(newGame.getId());
        assertThat(savedGame.getLastAskedQuestion())
                .isEqualToComparingOnlyGivenFields(questionDto, "content");
        assertThat(savedGame.getAskedQuestions())
                .containsExactly(savedGame.getLastAskedQuestion());
    }
}
