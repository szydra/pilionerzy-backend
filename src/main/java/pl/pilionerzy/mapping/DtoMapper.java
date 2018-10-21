package pl.pilionerzy.mapping;

import pl.pilionerzy.dto.NewQuestionDto;
import pl.pilionerzy.model.Question;

public interface DtoMapper {

    Question mapToModel(NewQuestionDto questionDto);

}
