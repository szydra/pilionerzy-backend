package pl.pilionerzy.repository;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import pl.pilionerzy.model.Game;

import java.time.LocalDateTime;
import java.util.Optional;

public interface GameRepository extends CrudRepository<Game, Long> {

    @Modifying
    @Query("update Game game set game.active = false where game.active = true and game.startTime < :time")
    int deactivateGamesStartedBefore(LocalDateTime time);

    @EntityGraph(attributePaths = "askedQuestions")
    @Query("select game from Game game where game.id = :id")
    Optional<Game> findByIdWithAskedQuestions(Long id);

    @EntityGraph(attributePaths = {"lastAskedQuestion", "lastAskedQuestion.answers"})
    @Query("select game from Game game where game.id = :id")
    Optional<Game> findByIdWithLastQuestionAndAnswers(Long id);

    @EntityGraph(attributePaths = {"usedLifelines", "lastAskedQuestion", "lastAskedQuestion.answers"})
    @Query("select game from Game game where game.id = :id")
    Optional<Game> findByIdWithUsedLifelines(Long id);
}
