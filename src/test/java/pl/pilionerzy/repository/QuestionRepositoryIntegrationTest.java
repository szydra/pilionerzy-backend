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
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static java.util.Collections.shuffle;
import static java.util.Comparator.comparing;
import static java.util.stream.Collectors.toList;
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
        // given: question with 3 answer and with null as active property
        Question question = prepareRandomQuestion();
        question.getAnswers().remove(0);

        // when: trying to save the question
        // then: constraints are violated
        assertThatExceptionOfType(ConstraintViolationException.class)
                .isThrownBy(() -> entityManager.persistAndFlush(question))
                .satisfies(exception -> assertThat(exception)
                        .hasViolation("active", "question must be active or inactive")
                        .hasViolation("answers", "question must have exactly 4 answers"));
    }

    @Test
    public void shouldNotSaveQuestionWithoutContent() {
        // given: question without content
        Question question = prepareRandomQuestion();
        question.activate();
        question.setContent(null);

        // when: trying to save the question
        // then: constraints are violated
        assertThatExceptionOfType(ConstraintViolationException.class)
                .isThrownBy(() -> entityManager.persistAndFlush(question))
                .satisfies(exception -> assertThat(exception)
                        .hasViolation("content", "question must have content"));
    }

    @Test
    public void shouldNotSaveQuestionWithEmptyContent() {
        // given: question with empty content
        Question question = prepareRandomQuestion();
        question.activate();
        question.setContent("");

        // when: trying to save the question
        // then: constraints are violated
        assertThatExceptionOfType(ConstraintViolationException.class)
                .isThrownBy(() -> entityManager.persistAndFlush(question))
                .satisfies(exception -> assertThat(exception)
                        .hasViolation("content", "question content length must be between 4 and 1023"));
    }

    @Test
    public void shouldNotSaveQuestionWithTooLongContent() {
        // given: question with too long content
        Question question = prepareRandomQuestion();
        question.activate();
        question.setContent(randomAlphanumeric(5000));

        // when: trying to save the question
        // then: constraints are violated
        assertThatExceptionOfType(ConstraintViolationException.class)
                .isThrownBy(() -> entityManager.persistAndFlush(question))
                .satisfies(exception -> assertThat(exception)
                        .hasViolation("content", "question content length must be between 4 and 1023"));
    }

    @Test
    public void shouldNotSaveQuestionWithoutAnswers() {
        // given: question without answers
        Question question = prepareRandomQuestion();
        question.activate();
        question.setAnswers(null);

        // when: trying to save the question
        // then: constraints are violated
        assertThatExceptionOfType(ConstraintViolationException.class)
                .isThrownBy(() -> entityManager.persistAndFlush(question))
                .satisfies(exception -> assertThat(exception)
                        .hasViolation("answers", "question must have answers"));
    }

    @Test
    public void shouldNotSaveQuestionWithoutCorrectAnswer() {
        // given: question without correct answer
        Question question = prepareRandomQuestion();
        question.activate();
        question.getAnswers().forEach(answer -> answer.setCorrect(false));

        // when: trying to save the question
        // then: constraints are violated
        assertThatExceptionOfType(ConstraintViolationException.class)
                .isThrownBy(() -> entityManager.persistAndFlush(question))
                .satisfies(exception -> assertThat(exception)
                        .hasViolation("", "one correct answer required"));
    }

    @Test
    public void shouldNotSaveQuestionWithTwoCorrectAnswers() {
        // given: question with two correct answers
        Question question = prepareRandomQuestion();
        question.activate();
        question.getAnswers().get(0).setCorrect(true);

        // when: trying to save the question
        // then: constraints are violated
        assertThatExceptionOfType(ConstraintViolationException.class)
                .isThrownBy(() -> entityManager.persistAndFlush(question))
                .satisfies(exception -> assertThat(exception)
                        .hasViolation("", "one correct answer required"));
    }

    @Test
    public void shouldNotFindInactiveQuestion() {
        // given: inactive question
        Question question = prepareRandomQuestion();
        question.deactivate();
        questionRepository.save(question);

        // when: looking for active questions
        var numberOfActiveQuestions = questionRepository.countByActive(true);
        var activeQuestions = questionRepository.findByActive(true, PageRequest.of(0, 1));

        // then: no questions are found
        assertThat(numberOfActiveQuestions).isZero();
        assertThat(activeQuestions).isNullOrEmpty();
    }

    @Test
    public void shouldFindActiveQuestion() {
        // given: active question
        Question question = prepareRandomQuestion();
        question.activate();
        questionRepository.save(question);

        // when: looking for active questions
        var numberOfActiveQuestions = questionRepository.countByActive(true);
        var activeQuestions = questionRepository.findByActive(true, PageRequest.of(0, 1));

        // then: exactly one question is found
        assertThat(numberOfActiveQuestions).isOne();
        assertThat(activeQuestions).containsExactly(question);
    }

    @Test
    public void shouldFindQuestionAndOrderPrefixes() {
        // given: saved question with random order of answers
        Question question = prepareRandomQuestion();
        question.activate();
        shuffle(question.getAnswers());
        Long id = entityManager.persistAndGetId(question, Long.class);
        entityManager.clear();

        // when: selecting the question
        Optional<Question> foundQuestion = questionRepository.findById(id);

        // then: answers are sorted according to prefixes
        assertThat(foundQuestion).hasValueSatisfying(q ->
                assertThat(q.getAnswers()).isSortedAccordingTo(comparing(Answer::getPrefix)));
    }

    @Test
    public void shouldNotSaveTheSameQuestionTwice() {
        // given: saved question
        Question question = prepareRandomQuestion();
        question.activate();
        entityManager.persistAndFlush(question);
        entityManager.detach(question);
        question.setId(null);

        // when: trying to insert it once again
        // then: unique constraint is violated
        assertThatExceptionOfType(PersistenceException.class)
                .isThrownBy(() -> entityManager.persistAndFlush(question));
    }

    @Test
    public void shouldNotSaveQuestionWithoutBusinessId() {
        // given: active question without business id
        Question question = prepareRandomQuestion();
        question.activate();
        question.setBusinessId(null);

        // when: trying to save the question
        // then: constraints are violated
        assertThatExceptionOfType(ConstraintViolationException.class)
                .isThrownBy(() -> entityManager.persistAndFlush(question))
                .satisfies(exception -> assertThat(exception)
                        .hasViolation("businessId", "question must have business id"));
    }

    @Test
    public void shouldNotSaveQuestionWithAnswerWithoutContent() {
        // given: question with an answer without content
        Question question = prepareRandomQuestion();
        question.activate();
        question.getAnswers().get(0).setContent(null);

        // when: trying to save the question
        // then: constraints are violated
        assertThatExceptionOfType(ConstraintViolationException.class)
                .isThrownBy(() -> entityManager.persistAndFlush(question))
                .satisfies(exception -> assertThat(exception)
                        .hasViolation("content", "answer must have content"));
    }

    @Test
    public void shouldNotSaveQuestionWithAnswerWithEmptyContent() {
        // given: question with an answer with empty content
        Question question = prepareRandomQuestion();
        question.activate();
        question.getAnswers().get(0).setContent("");

        // when: trying to save the question
        // then: constraints are violated
        assertThatExceptionOfType(ConstraintViolationException.class)
                .isThrownBy(() -> entityManager.persistAndFlush(question))
                .satisfies(exception -> assertThat(exception)
                        .hasViolation("content", "answer content length must be between 1 and 1023"));
    }

    @Test
    public void shouldNotSaveQuestionWithAnswerWithTooLongContent() {
        // given: question with an answer with too long content
        Question question = prepareRandomQuestion();
        question.activate();
        question.getAnswers().get(0).setContent(randomAlphanumeric(2000));

        // when: trying to save the question
        // then: constraints are violated
        assertThatExceptionOfType(ConstraintViolationException.class)
                .isThrownBy(() -> entityManager.persistAndFlush(question))
                .satisfies(exception -> assertThat(exception)
                        .hasViolation("content", "answer content length must be between 1 and 1023"));
    }

    @Test
    public void shouldNotSaveQuestionWithAnswerWithoutCorrectFlag() {
        // given: question with an answer without correct indicator
        Question question = prepareRandomQuestion();
        question.activate();
        question.getAnswers().get(0).setCorrect(null);

        // when: trying to save the question
        // then: constraints are violated
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
                .collect(toList());
    }

    private Answer getRandomAnswer(Prefix prefix) {
        Answer answer = new Answer();
        answer.setPrefix(prefix);
        answer.setContent(randomAlphanumeric(16));
        answer.setCorrect(prefix == C);
        return answer;
    }
}
