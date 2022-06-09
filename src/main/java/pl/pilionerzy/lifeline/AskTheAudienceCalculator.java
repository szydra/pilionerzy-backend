package pl.pilionerzy.lifeline;

import org.springframework.stereotype.Component;
import pl.pilionerzy.lifeline.model.AudienceAnswer;
import pl.pilionerzy.lifeline.model.PartialAudienceAnswer;
import pl.pilionerzy.model.Lifeline;
import pl.pilionerzy.model.Prefix;
import pl.pilionerzy.model.Question;

import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static com.google.common.collect.Iterables.getOnlyElement;
import static pl.pilionerzy.model.Lifeline.ASK_THE_AUDIENCE;

@Component
class AskTheAudienceCalculator extends AbstractLifelineProcessor<AudienceAnswer> {

    private static final int ONE_HUNDRED = 100;

    private final Random random = new Random();

    @Override
    protected AudienceAnswer getResult(Question question, Collection<Prefix> rejectedAnswers) {
        Map<Prefix, PartialAudienceAnswer> answers = new TreeMap<>();
        int correctAnswerRate = random.nextInt(ONE_HUNDRED);
        answers.put(question.getCorrectAnswer().getPrefix(), PartialAudienceAnswer.withVotes(correctAnswerRate));
        setRemainingRates(answers, rejectedAnswers);
        return new AudienceAnswer(answers);
    }

    private void setRemainingRates(Map<Prefix, PartialAudienceAnswer> answers, Collection<Prefix> rejectedAnswers) {
        List<Prefix> remainingAnswers = getRemainingAnswers(answers, rejectedAnswers);
        Collections.shuffle(remainingAnswers);
        int usedRate = getOnlyElement(answers.values()).getVotes();
        while (remainingAnswers.size() > 1) {
            int rate = random.nextInt(ONE_HUNDRED - usedRate);
            usedRate += rate;
            answers.put(remainingAnswers.remove(0), PartialAudienceAnswer.withVotes(rate));
        }
        answers.put(getOnlyElement(remainingAnswers), PartialAudienceAnswer.withVotes(ONE_HUNDRED - usedRate));
    }

    private List<Prefix> getRemainingAnswers(Map<Prefix, PartialAudienceAnswer> answers, Collection<Prefix> rejected) {
        return Arrays.stream(Prefix.values())
                .filter(Predicate.<Prefix>not(answers::containsKey).and(Predicate.not(rejected::contains)))
                .collect(Collectors.toList());
    }

    @Override
    public Lifeline type() {
        return ASK_THE_AUDIENCE;
    }
}
