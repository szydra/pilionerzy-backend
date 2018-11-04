package pl.pilionerzy.mapping;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import pl.pilionerzy.dto.GameDto;
import pl.pilionerzy.dto.NewQuestionDto;
import pl.pilionerzy.dto.QuestionDto;
import pl.pilionerzy.model.Game;
import pl.pilionerzy.model.Question;

@Component
class MultiObjectMapper implements DtoMapper {

    private GameMapper gameMapper;
    private QuestionMapper questionMapper;

    @Autowired
    public MultiObjectMapper(GameMapper gameMapper, QuestionMapper questionMapper) {
        this.gameMapper = gameMapper;
        this.questionMapper = questionMapper;
    }

    @Override
    public GameDto mapToDto(Game game) {
        return gameMapper.modelToDto(game);
    }

    @Override
    public QuestionDto mapToDto(Question question) {
        return questionMapper.modelToDto(question, new LoopAvoidingContext());
    }

    @Override
    public Question mapToModel(NewQuestionDto questionDto) {
        return questionMapper.dtoToModel(questionDto, new LoopAvoidingContext());
    }

}
