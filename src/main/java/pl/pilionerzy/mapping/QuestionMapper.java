package pl.pilionerzy.mapping;

import com.google.common.hash.Hasher;
import com.google.common.hash.Hashing;
import org.mapstruct.*;
import pl.pilionerzy.dto.NewQuestionDto;
import pl.pilionerzy.dto.QuestionDto;
import pl.pilionerzy.model.Question;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.mapstruct.ReportingPolicy.IGNORE;

@Mapper(componentModel = "spring", unmappedTargetPolicy = IGNORE, uses = {AnswerMapper.class})
public interface QuestionMapper {

    @Mapping(target = "active", constant = "false")
    Question dtoToModel(NewQuestionDto questionDto, @Context LoopAvoidingContext context);

    Question dtoToModel(QuestionDto questionDto, @Context LoopAvoidingContext context);

    NewQuestionDto modelToNewDto(Question question, @Context LoopAvoidingContext context);

    QuestionDto modelToDto(Question question, @Context LoopAvoidingContext context);

    @AfterMapping
    default void setBusinessId(NewQuestionDto questionDto, @MappingTarget Question question) {
        question.setBusinessId(calculateHash(questionDto));
    }

    @SuppressWarnings("UnstableApiUsage")
    default String calculateHash(NewQuestionDto questionDto) {
        Hasher hasher = Hashing.murmur3_128().newHasher().putString(questionDto.getContent(), UTF_8);
        questionDto.getAnswers().forEach(answer -> hasher.putString(answer.getContent(), UTF_8));
        return hasher.hash().toString();
    }
}
