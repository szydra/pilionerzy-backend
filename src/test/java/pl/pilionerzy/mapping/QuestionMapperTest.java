package pl.pilionerzy.mapping;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import pl.pilionerzy.dto.NewAnswerDto;
import pl.pilionerzy.dto.NewQuestionDto;
import pl.pilionerzy.model.Prefix;
import pl.pilionerzy.model.Question;

import java.util.ArrayList;
import java.util.List;

import static org.apache.commons.lang3.RandomStringUtils.randomAlphanumeric;
import static pl.pilionerzy.assertion.Assertions.assertThat;

@RunWith(SpringRunner.class)
@ContextConfiguration(classes = {AnswerMapperImpl.class, QuestionMapperImpl.class})
public class QuestionMapperTest {

    @Autowired
    private QuestionMapper questionMapper;

    @Test
    public void shouldCalculateHash() {
        NewQuestionDto questionDto = createNewQuestionDto();

        Question question = questionMapper.dtoToModel(questionDto, new LoopAvoidingContext());

        String hash = questionMapper.calculateHash(questionDto);
        assertThat(question.getHash()).isEqualTo(hash).hasSize(32);
    }

    @Test
    public void shouldNotActivateNewQuestion() {
        NewQuestionDto questionDto = createNewQuestionDto();

        Question question = questionMapper.dtoToModel(questionDto, new LoopAvoidingContext());

        assertThat(question.getActive()).isFalse();
    }

    @Test
    public void shouldSetQuestionInAllAnswers() {
        NewQuestionDto questionDto = createNewQuestionDto();

        Question question = questionMapper.dtoToModel(questionDto, new LoopAvoidingContext());

        assertThat(question.getAnswers())
                .hasSize(4)
                .allMatch(answer -> answer.getQuestion() == question);
    }

    private NewQuestionDto createNewQuestionDto() {
        NewQuestionDto questionDto = new NewQuestionDto();
        questionDto.setContent(randomAlphanumeric(32));
        List<NewAnswerDto> answers = new ArrayList<>(4);
        for (Prefix prefix : Prefix.values()) {
            NewAnswerDto answer = new NewAnswerDto();
            answer.setPrefix(prefix);
            answer.setContent(randomAlphanumeric(32));
            answer.setQuestion(questionDto);
            answers.add(answer);
        }
        questionDto.setAnswers(answers);
        questionDto.setCorrectAnswer(Prefix.A);
        return questionDto;
    }
}
