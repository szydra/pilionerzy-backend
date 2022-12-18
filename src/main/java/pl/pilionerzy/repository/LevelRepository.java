package pl.pilionerzy.repository;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.repository.CrudRepository;
import pl.pilionerzy.model.Level;

import java.util.List;

public interface LevelRepository extends CrudRepository<Level, Integer> {

    @Cacheable("levels")
    List<Level> findByOrderByIdAsc();
}
