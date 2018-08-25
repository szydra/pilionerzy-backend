package pl.pilionerzy.dao;

import org.springframework.data.repository.CrudRepository;
import pl.pilionerzy.model.Game;

public interface GameDao extends CrudRepository<Game, Long> {

}
