package pl.pilionerzy.mapping;

import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import pl.pilionerzy.dto.AnswerDto;
import pl.pilionerzy.model.Answer;

@Mapper(componentModel = "spring", uses = {QuestionMapper.class})
public interface AnswerMapper {

    @Mapping(target = "id", ignore = true)
    Answer dtoToModel(AnswerDto answerDto, @Context LoopAvoidingContext context);

    AnswerDto modelToDto(Answer answer, @Context LoopAvoidingContext context);

}
