package pl.pilionerzy.dao;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import pl.pilionerzy.model.Answer;
import pl.pilionerzy.model.Prefix;
import pl.pilionerzy.model.Question;

import javax.validation.ConstraintViolationException;

import java.util.*;
import java.util.stream.Collectors;

import static org.apache.commons.lang3.RandomStringUtils.randomAlphanumeric;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static pl.pilionerzy.assertion.Assertions.assertThat;

@RunWith(SpringRunner.class)
@DataJpaTest
@ActiveProfiles("test")
public class QuestionDaoIntegrationTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private QuestionDao questionDao;

    @Test
    public void shouldNotSaveIncompleteQuestion() {
        Question question = prepareSampleQuestion();
        question.getAnswers().remove(0);

        assertThatExceptionOfType(ConstraintViolationException.class)
                .isThrownBy(() -> entityManager.persistAndFlush(question))
                .satisfies(exception -> assertThat(exception)
                        .hasViolation("active", "must not be null")
                        .hasViolation("answers", "size must be between 4 and 4"));
    }

    @Test
    public void shouldNotSaveQuestionWithoutContent() {
        Question question = prepareSampleQuestion();
        question.setActive(true);
        question.setContent(null);

        assertThatExceptionOfType(ConstraintViolationException.class)
                .isThrownBy(() -> entityManager.persistAndFlush(question))
                .satisfies(exception -> assertThat(exception)
                        .hasViolation("content", "must not be null"));
    }

    @Test
    public void shouldNotSaveQuestionWithEmptyContent() {
        Question question = prepareSampleQuestion();
        question.setActive(true);
        question.setContent("");

        assertThatExceptionOfType(ConstraintViolationException.class)
                .isThrownBy(() -> entityManager.persistAndFlush(question))
                .satisfies(exception -> assertThat(exception)
                        .hasViolation("content", "question content length must be between 4 and 1023"));
    }

    @Test
    public void shouldNotSaveQuestionWithTooLongContent() {
        Question question = prepareSampleQuestion();
        question.setActive(true);
        question.setContent(randomAlphanumeric(5000));

        assertThatExceptionOfType(ConstraintViolationException.class)
                .isThrownBy(() -> entityManager.persistAndFlush(question))
                .satisfies(exception -> assertThat(exception)
                        .hasViolation("content", "question content length must be between 4 and 1023"));
    }

    @Test
    public void shouldNotGetInactiveQuestion() {
        Question question = prepareSampleQuestion();
        question.setActive(false);

        questionDao.save(question);

        assertThat(questionDao.countByActive(true))
                .isZero();
        assertThat(questionDao.findByActive(true, PageRequest.of(0, 1)))
                .isNullOrEmpty();
    }

    @Test
    public void shouldGetActiveQuestion() {
        Question question = prepareSampleQuestion();
        question.activate();

        questionDao.save(question);

        assertThat(questionDao.countByActive(true))
                .isOne();
        assertThat(questionDao.findByActive(true, PageRequest.of(0, 1)))
                .containsExactly(question);
    }

    @Test
    public void shouldFindQuestionAndOrderPrefixes() {
        Question sampleQuestion = prepareSampleQuestion();
        sampleQuestion.activate();
        Collections.reverse(sampleQuestion.getAnswers());

        Long id = (Long) entityManager.persistAndGetId(sampleQuestion);
        entityManager.clear();
        Optional<Question> foundQuestion = questionDao.findById(id);

        assertThat(foundQuestion)
                .hasValueSatisfying(question -> assertThat(question.getAnswers())
                        .isSortedAccordingTo(Comparator.comparing(Answer::getPrefix)));
    }

    private Question prepareSampleQuestion() {
        Question question = new Question();
        List<Answer> answers = prepareAnswers();
        answers.forEach(answer -> answer.setQuestion(question));
        question.setAnswers(answers);
        question.setContent("sample content");
        question.setCorrectAnswer(Prefix.C);
        return question;
    }

    private List<Answer> prepareAnswers() {
        return Arrays.stream(Prefix.values())
                .map(this::mapToAnswer)
                .collect(Collectors.toList());
    }

    private Answer mapToAnswer(Prefix prefix) {
        Answer answer = new Answer();
        answer.setPrefix(prefix);
        answer.setContent(prefix.toString());
        return answer;
    }
}
