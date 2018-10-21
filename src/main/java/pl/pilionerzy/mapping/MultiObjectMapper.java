package pl.pilionerzy.mapping;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import pl.pilionerzy.dto.NewQuestionDto;
import pl.pilionerzy.model.Question;

@Component
class MultiObjectMapper implements DtoMapper {

    private QuestionMapper questionMapper;

    @Autowired
    public MultiObjectMapper(QuestionMapper questionMapper) {
        this.questionMapper = questionMapper;
    }

    @Override
    public Question mapToModel(NewQuestionDto questionDto) {
        return questionMapper.dtoToModel(questionDto, new LoopAvoidingContext());
    }

}
