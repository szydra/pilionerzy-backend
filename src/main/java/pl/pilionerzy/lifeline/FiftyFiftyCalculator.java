package pl.pilionerzy.lifeline;

import org.springframework.stereotype.Component;
import pl.pilionerzy.lifeline.model.FiftyFiftyResult;
import pl.pilionerzy.model.Lifeline;
import pl.pilionerzy.model.Prefix;
import pl.pilionerzy.model.Question;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Random;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static pl.pilionerzy.model.Lifeline.FIFTY_FIFTY;

@Component
class FiftyFiftyCalculator extends AbstractLifelineProcessor<FiftyFiftyResult> {

    private final Random random = new Random();

    @Override
    protected FiftyFiftyResult getResult(Question question, Collection<Prefix> rejectedAnswers) {
        Prefix correctAnswerPrefix = question.getCorrectAnswer().getPrefix();
        List<Prefix> incorrectPrefixes = Arrays.stream(Prefix.values())
                .filter(Predicate.isEqual(correctAnswerPrefix).negate())
                .collect(Collectors.toList());
        int randomIndex = random.nextInt(3);
        incorrectPrefixes.remove(randomIndex);
        return new FiftyFiftyResult(incorrectPrefixes);
    }

    @Override
    public Lifeline type() {
        return FIFTY_FIFTY;
    }
}
