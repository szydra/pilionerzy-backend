package pl.pilionerzy.mapping;

import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import pl.pilionerzy.dto.AnswerDto;
import pl.pilionerzy.dto.NewAnswerDto;
import pl.pilionerzy.model.Answer;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE, uses = {QuestionMapper.class})
public interface AnswerMapper {

    Answer dtoToModel(NewAnswerDto answerDto, @Context LoopAvoidingContext context);

    Answer dtoToModel(AnswerDto answerDto, @Context LoopAvoidingContext context);

    NewAnswerDto modelToNewDto(Answer answer, @Context LoopAvoidingContext context);

    AnswerDto modelToDto(Answer answer, @Context LoopAvoidingContext context);

}
