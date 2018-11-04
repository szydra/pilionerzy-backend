package pl.pilionerzy.mapping;

import org.mapstruct.Mapper;
import pl.pilionerzy.dto.GameDto;
import pl.pilionerzy.model.Game;

@Mapper(componentModel = "spring")
public interface GameMapper {

    GameDto modelToDto(Game game);

}
