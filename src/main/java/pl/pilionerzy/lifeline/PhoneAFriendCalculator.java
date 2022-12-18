package pl.pilionerzy.lifeline;

import org.springframework.stereotype.Component;
import pl.pilionerzy.exception.LifelineException;
import pl.pilionerzy.lifeline.model.FriendsAnswer;
import pl.pilionerzy.model.Lifeline;
import pl.pilionerzy.model.Prefix;
import pl.pilionerzy.model.Question;

import java.util.Arrays;
import java.util.Collection;
import java.util.Random;
import java.util.function.Predicate;

import static pl.pilionerzy.model.Lifeline.PHONE_A_FRIEND;

@Component
class PhoneAFriendCalculator extends AbstractLifelineProcessor<FriendsAnswer> {

    private static final int LOWEST_WISDOM = 30;
    private static final int SUPREME_WISDOM = 100;

    private final Random random = new Random();

    @Override
    protected FriendsAnswer getResult(Question question, Collection<Prefix> rejectedAnswers) {
        Prefix correctAnswer = question.getCorrectAnswer().getPrefix();
        if (rejectedAnswers.contains(correctAnswer)) {
            throw new IllegalArgumentException("Correct answer prefix cannot be rejected");
        }
        final int friendsWisdom = LOWEST_WISDOM + 10 * random.nextInt(1 + (SUPREME_WISDOM - LOWEST_WISDOM) / 10);
        int rawFriendsAnswer = random.nextInt(SUPREME_WISDOM);
        if (rawFriendsAnswer < friendsWisdom) {
            return new FriendsAnswer(correctAnswer, friendsWisdom);
        } else {
            Prefix[] allPrefixes = Prefix.values();
            return Arrays.stream(allPrefixes)
                    .filter(Predicate.not(rejectedAnswers::contains))
                    .filter(Predicate.isEqual(correctAnswer).negate())
                    .skip(random.nextInt(allPrefixes.length - rejectedAnswers.size() - 1))
                    .findFirst()
                    .map(prefix -> new FriendsAnswer(prefix, friendsWisdom))
                    .orElseThrow(() -> new LifelineException("Cannot calculate friend's answer"));
        }
    }

    @Override
    public Lifeline type() {
        return PHONE_A_FRIEND;
    }
}
