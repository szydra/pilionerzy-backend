package pl.pilionerzy.mapping;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import pl.pilionerzy.dto.NewAnswerDto;
import pl.pilionerzy.dto.NewQuestionDto;
import pl.pilionerzy.model.Prefix;

import java.util.ArrayList;
import java.util.List;

import static org.apache.commons.lang3.RandomStringUtils.randomAlphanumeric;
import static pl.pilionerzy.assertion.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {QuestionMapperImpl.class})
class QuestionMapperTest {

    @Autowired
    private QuestionMapper questionMapper;

    @Test
    void shouldCalculateHash() {
        // given
        var questionDto = createNewQuestionDto();

        // when
        var question = questionMapper.dtoToModel(questionDto, new LoopAvoidingContext());

        // then
        var hash = questionMapper.calculateHash(questionDto);
        assertThat(question.getHash()).isEqualTo(hash).hasSize(32);
    }

    @Test
    void shouldNotActivateNewQuestion() {
        // given
        var questionDto = createNewQuestionDto();

        // when
        var question = questionMapper.dtoToModel(questionDto, new LoopAvoidingContext());

        // then
        assertThat(question.getActive()).isFalse();
    }

    @Test
    void shouldSetQuestionInAllAnswers() {
        // given
        var questionDto = createNewQuestionDto();

        // when
        var question = questionMapper.dtoToModel(questionDto, new LoopAvoidingContext());

        // then
        assertThat(question.getAnswers())
                .hasSize(4)
                .allMatch(answer -> answer.getQuestion() == question);
    }

    private NewQuestionDto createNewQuestionDto() {
        var questionDto = new NewQuestionDto();
        questionDto.setContent(randomAlphanumeric(32));
        List<NewAnswerDto> answers = new ArrayList<>(4);
        for (Prefix prefix : Prefix.values()) {
            var answer = new NewAnswerDto();
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
