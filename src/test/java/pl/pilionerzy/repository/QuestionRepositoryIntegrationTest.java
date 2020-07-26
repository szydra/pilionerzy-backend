package pl.pilionerzy.repository;

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

import javax.persistence.PersistenceException;
import javax.validation.ConstraintViolationException;
import java.util.*;
import java.util.stream.Collectors;

import static org.apache.commons.lang3.RandomStringUtils.randomAlphanumeric;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static pl.pilionerzy.assertion.Assertions.assertThat;
import static pl.pilionerzy.model.Prefix.C;

@RunWith(SpringRunner.class)
@DataJpaTest
@ActiveProfiles("test")
public class QuestionRepositoryIntegrationTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private QuestionRepository questionRepository;

    @Test
    public void shouldNotSaveIncompleteQuestion() {
        Question question = prepareRandomQuestion();
        question.getAnswers().remove(0);

        assertThatExceptionOfType(ConstraintViolationException.class)
                .isThrownBy(() -> entityManager.persistAndFlush(question))
                .satisfies(exception -> assertThat(exception)
                        .hasViolation("active", "question must be active or inactive")
                        .hasViolation("answers", "question must have exactly 4 answers"));
    }

    @Test
    public void shouldNotSaveQuestionWithoutContent() {
        Question question = prepareRandomQuestion();
        question.activate();
        question.setContent(null);

        assertThatExceptionOfType(ConstraintViolationException.class)
                .isThrownBy(() -> entityManager.persistAndFlush(question))
                .satisfies(exception -> assertThat(exception)
                        .hasViolation("content", "question must have content"));
    }

    @Test
    public void shouldNotSaveQuestionWithEmptyContent() {
        Question question = prepareRandomQuestion();
        question.activate();
        question.setContent("");

        assertThatExceptionOfType(ConstraintViolationException.class)
                .isThrownBy(() -> entityManager.persistAndFlush(question))
                .satisfies(exception -> assertThat(exception)
                        .hasViolation("content", "question content length must be between 4 and 1023"));
    }

    @Test
    public void shouldNotSaveQuestionWithTooLongContent() {
        Question question = prepareRandomQuestion();
        question.activate();
        question.setContent(randomAlphanumeric(5000));

        assertThatExceptionOfType(ConstraintViolationException.class)
                .isThrownBy(() -> entityManager.persistAndFlush(question))
                .satisfies(exception -> assertThat(exception)
                        .hasViolation("content", "question content length must be between 4 and 1023"));
    }

    @Test
    public void shouldNotSaveQuestionWithoutAnswers() {
        Question question = prepareRandomQuestion();
        question.activate();
        question.setAnswers(null);

        assertThatExceptionOfType(ConstraintViolationException.class)
                .isThrownBy(() -> entityManager.persistAndFlush(question))
                .satisfies(exception -> assertThat(exception)
                        .hasViolation("answers", "question must have answers"));
    }

    @Test
    public void shouldNotSaveQuestionWithoutCorrectAnswer() {
        Question question = prepareRandomQuestion();
        question.activate();
        question.getAnswers().forEach(answer -> answer.setCorrect(false));

        assertThatExceptionOfType(ConstraintViolationException.class)
                .isThrownBy(() -> entityManager.persistAndFlush(question))
                .satisfies(exception -> assertThat(exception)
                        .hasViolation("", "one correct answer required"));
    }

    @Test
    public void shouldNotSaveQuestionWithTwoCorrectAnswers() {
        Question question = prepareRandomQuestion();
        question.activate();
        question.getAnswers().get(0).setCorrect(true);

        assertThatExceptionOfType(ConstraintViolationException.class)
                .isThrownBy(() -> entityManager.persistAndFlush(question))
                .satisfies(exception -> assertThat(exception)
                        .hasViolation("", "one correct answer required"));
    }

    @Test
    public void shouldNotFindInactiveQuestion() {
        Question question = prepareRandomQuestion();
        question.deactivate();

        questionRepository.save(question);

        assertThat(questionRepository.countByActive(true))
                .isZero();
        assertThat(questionRepository.findByActive(true, PageRequest.of(0, 1)))
                .isNullOrEmpty();
    }

    @Test
    public void shouldFindActiveQuestion() {
        Question question = prepareRandomQuestion();
        question.activate();

        questionRepository.save(question);

        assertThat(questionRepository.countByActive(true))
                .isOne();
        assertThat(questionRepository.findByActive(true, PageRequest.of(0, 1)))
                .containsExactly(question);
    }

    @Test
    public void shouldFindQuestionAndOrderPrefixes() {
        Question question = prepareRandomQuestion();
        question.activate();
        Collections.reverse(question.getAnswers());

        Long id = entityManager.persistAndGetId(question, Long.class);
        entityManager.clear();
        Optional<Question> found = questionRepository.findById(id);

        assertThat(found).hasValueSatisfying(q -> assertThat(q.getAnswers())
                .isSortedAccordingTo(Comparator.comparing(Answer::getPrefix)));
    }

    @Test
    public void shouldNotSaveTheSameQuestionTwice() {
        Question question = prepareRandomQuestion();
        question.activate();
        entityManager.persistAndFlush(question);
        entityManager.detach(question);
        question.setId(null);

        assertThatExceptionOfType(PersistenceException.class)
                .isThrownBy(() -> entityManager.persistAndFlush(question));
    }

    @Test
    public void shouldNotSaveQuestionWithoutBusinessId() {
        Question question = prepareRandomQuestion();
        question.activate();
        question.setBusinessId(null);

        assertThatExceptionOfType(ConstraintViolationException.class)
                .isThrownBy(() -> entityManager.persistAndFlush(question))
                .satisfies(exception -> assertThat(exception)
                        .hasViolation("businessId", "question must have business id"));
    }

    @Test
    public void shouldNotSaveQuestionWithAnswerWithoutContent() {
        Question question = prepareRandomQuestion();
        question.activate();
        question.getAnswers().get(0).setContent(null);

        assertThatExceptionOfType(ConstraintViolationException.class)
                .isThrownBy(() -> entityManager.persistAndFlush(question))
                .satisfies(exception -> assertThat(exception)
                        .hasViolation("content", "answer must have content"));
    }

    @Test
    public void shouldNotSaveQuestionWithAnswerWithEmptyContent() {
        Question question = prepareRandomQuestion();
        question.activate();
        question.getAnswers().get(0).setContent("");

        assertThatExceptionOfType(ConstraintViolationException.class)
                .isThrownBy(() -> entityManager.persistAndFlush(question))
                .satisfies(exception -> assertThat(exception)
                        .hasViolation("content", "answer content length must be between 1 and 1023"));
    }

    @Test
    public void shouldNotSaveQuestionWithAnswerWithTooLongContent() {
        Question question = prepareRandomQuestion();
        question.activate();
        question.getAnswers().get(0).setContent(randomAlphanumeric(2000));

        assertThatExceptionOfType(ConstraintViolationException.class)
                .isThrownBy(() -> entityManager.persistAndFlush(question))
                .satisfies(exception -> assertThat(exception)
                        .hasViolation("content", "answer content length must be between 1 and 1023"));
    }

    @Test
    public void shouldNotSaveQuestionWithAnswerWithoutCorrectFlag() {
        Question question = prepareRandomQuestion();
        question.activate();
        question.getAnswers().get(0).setCorrect(null);

        assertThatExceptionOfType(ConstraintViolationException.class)
                .isThrownBy(() -> entityManager.persistAndFlush(question))
                .satisfies(exception -> assertThat(exception)
                        .hasViolation("correct", "answer must be correct or incorrect"));
    }

    private Question prepareRandomQuestion() {
        Question question = new Question();
        List<Answer> answers = prepareRandomAnswers();
        answers.forEach(answer -> answer.setQuestion(question));
        question.setAnswers(answers);
        question.setContent(randomAlphanumeric(32));
        question.setBusinessId(randomAlphanumeric(32));
        return question;
    }

    private List<Answer> prepareRandomAnswers() {
        return Arrays.stream(Prefix.values())
                .map(this::getRandomAnswer)
                .collect(Collectors.toList());
    }

    private Answer getRandomAnswer(Prefix prefix) {
        Answer answer = new Answer();
        answer.setPrefix(prefix);
        answer.setContent(randomAlphanumeric(16));
        answer.setCorrect(prefix == C);
        return answer;
    }
}
