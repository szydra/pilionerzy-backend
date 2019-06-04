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

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

@RunWith(SpringRunner.class)
@DataJpaTest
@ActiveProfiles("test")
public class QuestionDaoTest {

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
                // assertion for answers
                .withMessageContaining("size must be between 4 and 4")
                // assertion for active
                .withMessageContaining("must not be null");
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

        Long id = questionDao.save(sampleQuestion).getId();
        entityManager.clear();
        Optional<Question> foundQuestion = questionDao.findById(id);

        assertThat(foundQuestion)
                .hasValueSatisfying(question ->
                        assertThat(question.getAnswers())
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
