package pl.pilionerzy.lifeline;

import com.google.common.base.Predicates;
import com.google.common.collect.Iterables;
import org.springframework.stereotype.Component;
import pl.pilionerzy.lifeline.model.AudienceAnswer;
import pl.pilionerzy.lifeline.model.PartialAudienceAnswer;
import pl.pilionerzy.model.Prefix;
import pl.pilionerzy.model.Question;

import java.util.*;
import java.util.stream.Collectors;

@Component
class AskTheAudienceCalculator {

    private static final int ONE_HUNDRED = 100;
    private final Random random = new Random();

    AudienceAnswer getAnswer(Question question, Collection<Prefix> rejectedAnswers) {
        Map<Prefix, PartialAudienceAnswer> answers = new TreeMap<>();
        int correctAnswerRate = random.nextInt(ONE_HUNDRED);
        answers.put(question.getCorrectAnswer(), PartialAudienceAnswer.withVotes(correctAnswerRate));
        setRemainingRates(answers, rejectedAnswers);
        return new AudienceAnswer(answers);
    }

    private void setRemainingRates(Map<Prefix, PartialAudienceAnswer> answers, Collection<Prefix> rejectedAnswers) {
        List<Prefix> remainingAnswers = getRemainingAnswers(answers, rejectedAnswers);
        Collections.shuffle(remainingAnswers);
        int usedRate = Iterables.getOnlyElement(answers.values()).getVotes();
        while (remainingAnswers.size() > 1) {
            int rate = random.nextInt(ONE_HUNDRED - usedRate);
            usedRate += rate;
            answers.put(remainingAnswers.remove(0), PartialAudienceAnswer.withVotes(rate));
        }
        answers.put(Iterables.getOnlyElement(remainingAnswers), PartialAudienceAnswer.withVotes(ONE_HUNDRED - usedRate));
    }

    @SuppressWarnings("Guava")
    private List<Prefix> getRemainingAnswers(Map<Prefix, PartialAudienceAnswer> answers, Collection<Prefix> rejected) {
        return Arrays.stream(Prefix.values())
                .filter(Predicates.not(answers::containsKey))
                .filter(Predicates.not(rejected::contains))
                .collect(Collectors.toList());
    }
}
