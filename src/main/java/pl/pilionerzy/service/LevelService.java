package pl.pilionerzy.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pl.pilionerzy.model.Level;
import pl.pilionerzy.repository.LevelRepository;

import java.util.List;

import static java.util.Comparator.comparingInt;

@Service
@RequiredArgsConstructor
public class LevelService {

    private final LevelRepository levelRepository;

    public List<Level> getAllLevels() {
        return levelRepository.findByOrderByIdAsc();
    }

    public int getGuaranteedLevel(int currentLevel) {
        var levels = getAllLevels();
        var highestLevel = levels.stream().max(comparingInt(Level::getId)).orElseThrow();
        if (currentLevel < 0 || currentLevel > highestLevel.getId()) {
            throw new IllegalArgumentException("Game level has to be between 0 and " + highestLevel.getId());
        }
        while (currentLevel >= 0 && !levels.get(currentLevel).getGuaranteed()) {
            currentLevel--;
        }
        if (currentLevel < 0) {
            throw new IllegalStateException("Cannot find guaranteed level");
        }
        return currentLevel;
    }
}
