package pl.pilionerzy.dao;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import pl.pilionerzy.model.Game;

import java.time.LocalDateTime;

public interface GameDao extends CrudRepository<Game, Long> {

    @Modifying
    @Query("update Game g set g.active = false where g.active = true and g.startTime < ?1")
    int deactivateGamesStartedBefore(LocalDateTime time);

}
