package pl.pilionerzy.util;

import java.util.Arrays;
import java.util.List;

public class LevelUtils {

    private static final int HIGHEST_LEVEL = 12;

    private static final List<Integer> GUARANTEED_LEVELS = Arrays.asList(0, 2, 7, 12);

    public static int getNextLevel(int currentLevel) {
        return currentLevel + 1;
    }

    public static int getGuaranteedLevel(int currentLevel) {
        if (currentLevel < 0 || currentLevel > 12) {
            throw new IllegalArgumentException("Game level has to be between 0 and 12");
        }
        while (!GUARANTEED_LEVELS.contains(currentLevel)) {
            currentLevel--;
        }
        return currentLevel;
    }

    public static boolean isHighestLevel(int currentLevel) {
        return currentLevel == HIGHEST_LEVEL;
    }
}
