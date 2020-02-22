package pl.pilionerzy.mapping;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import pl.pilionerzy.dto.GameDto;
import pl.pilionerzy.model.Game;

@Mapper(componentModel = "spring")
public interface GameMapper {

    @Mapping(source = "lastAskedQuestion.correctAnswer.prefix", target = "correctAnswer")
    GameDto modelToDto(Game game);

}
