package pl.pilionerzy.util.lifeline;

import com.google.common.base.Predicates;
import com.google.common.collect.Iterables;
import pl.pilionerzy.model.AudienceAnswer;
import pl.pilionerzy.model.Prefix;
import pl.pilionerzy.model.Question;

import java.util.*;
import java.util.stream.Collectors;

public class AskTheAudienceCalculator {

    private static final int ONE_HUNDRED = 100;
    private final Random random = new Random();

    public Map<Prefix, AudienceAnswer> getAnswer(Question question, Collection<Prefix> rejectedAnswers) {
        Map<Prefix, AudienceAnswer> answer = new TreeMap<>();
        int correctAnswerRate = random.nextInt(ONE_HUNDRED);
        answer.put(question.getCorrectAnswer(), AudienceAnswer.withVotes(correctAnswerRate));
        setRemainingRates(answer, rejectedAnswers);
        return answer;
    }

    private void setRemainingRates(Map<Prefix, AudienceAnswer> answer, Collection<Prefix> rejectedAnswers) {
        List<Prefix> remainingAnswers = getRemainingAnswers(answer, rejectedAnswers);
        Collections.shuffle(remainingAnswers);
        Random random = this.random;
        int usedRate = Iterables.getOnlyElement(answer.values()).getVotes();
        while (remainingAnswers.size() > 1) {
            int rate = random.nextInt(ONE_HUNDRED - usedRate);
            usedRate += rate;
            answer.put(remainingAnswers.remove(0), AudienceAnswer.withVotes(rate));
        }
        answer.put(Iterables.getOnlyElement(remainingAnswers), AudienceAnswer.withVotes(ONE_HUNDRED - usedRate));
    }

    @SuppressWarnings("Guava")
    private List<Prefix> getRemainingAnswers(Map<Prefix, AudienceAnswer> answer, Collection<Prefix> rejected) {
        return Arrays.stream(Prefix.values())
                .filter(Predicates.not(answer::containsKey))
                .filter(Predicates.not(rejected::contains))
                .collect(Collectors.toList());
    }
}
