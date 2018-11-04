package pl.pilionerzy.mapping;

import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import pl.pilionerzy.dto.NewQuestionDto;
import pl.pilionerzy.dto.QuestionDto;
import pl.pilionerzy.model.Question;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE, uses = {AnswerMapper.class})
public interface QuestionMapper {

    @Mapping(target = "active", constant = "false")
    Question dtoToModel(NewQuestionDto questionDto, @Context LoopAvoidingContext context);

    Question dtoToModel(QuestionDto questionDto, @Context LoopAvoidingContext context);

    NewQuestionDto modelToNewDto(Question question, @Context LoopAvoidingContext context);

    QuestionDto modelToDto(Question question, @Context LoopAvoidingContext context);

}
