package pl.pilionerzy.util.lifeline;

import pl.pilionerzy.model.Prefix;
import pl.pilionerzy.model.Question;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Random;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class FiftyFiftyCalculator {

    public static Collection<Prefix> getPrefixesToDiscard(Question question) {
        Prefix correctAnswerPrefix = question.getCorrectAnswer();
        List<Prefix> incorrectPrefixes = Arrays.stream(Prefix.values())
                .filter(Predicate.isEqual(correctAnswerPrefix).negate())
                .collect(Collectors.toList());
        int randomIndex = new Random().nextInt(3);
        incorrectPrefixes.remove(randomIndex);
        return incorrectPrefixes;
    }
}
