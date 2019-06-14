package pl.pilionerzy.mapping;

import pl.pilionerzy.dto.GameDto;
import pl.pilionerzy.dto.NewQuestionDto;
import pl.pilionerzy.dto.QuestionDto;
import pl.pilionerzy.model.Game;
import pl.pilionerzy.model.Question;

public interface DtoMapper {

    GameDto mapToDto(Game game);

    QuestionDto mapToDto(Question question);

    NewQuestionDto mapToNewDto(Question question);

    Question mapToModel(NewQuestionDto questionDto);

}
