package pl.pilionerzy.lifeline;

import org.springframework.stereotype.Component;
import pl.pilionerzy.lifeline.model.FiftyFiftyResult;
import pl.pilionerzy.model.Prefix;
import pl.pilionerzy.model.Question;

import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@Component
class FiftyFiftyCalculator {

    FiftyFiftyResult getPrefixesToDiscard(Question question) {
        Prefix correctAnswerPrefix = question.getCorrectAnswer().getPrefix();
        List<Prefix> incorrectPrefixes = Arrays.stream(Prefix.values())
                .filter(Predicate.isEqual(correctAnswerPrefix).negate())
                .collect(Collectors.toList());
        int randomIndex = new Random().nextInt(3);
        incorrectPrefixes.remove(randomIndex);
        return new FiftyFiftyResult(incorrectPrefixes);
    }
}
