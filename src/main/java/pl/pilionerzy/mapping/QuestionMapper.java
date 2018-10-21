package pl.pilionerzy.mapping;

import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import pl.pilionerzy.dto.NewQuestionDto;
import pl.pilionerzy.model.Question;

@Mapper(componentModel = "spring", uses = {AnswerMapper.class})
public interface QuestionMapper {

    @Mapping(target = "active", constant = "false")
    @Mapping(target = "id", ignore = true)
    Question dtoToModel(NewQuestionDto questionDto, @Context LoopAvoidingContext context);

    NewQuestionDto modelToDto(Question question, @Context LoopAvoidingContext context);

}
