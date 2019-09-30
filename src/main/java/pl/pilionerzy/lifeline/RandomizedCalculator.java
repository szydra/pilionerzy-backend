package pl.pilionerzy.lifeline;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import pl.pilionerzy.lifeline.model.AudienceAnswer;
import pl.pilionerzy.lifeline.model.FiftyFiftyResult;
import pl.pilionerzy.lifeline.model.FriendsAnswer;
import pl.pilionerzy.model.Prefix;
import pl.pilionerzy.model.Question;

import java.util.Set;

@Component
@RequiredArgsConstructor
class RandomizedCalculator implements Calculator {

    private final FiftyFiftyCalculator fiftyFiftyCalculator;
    private final PhoneAFriendCalculator phoneAFriendCalculator;
    private final AskTheAudienceCalculator askTheAudienceCalculator;

    @Override
    public FiftyFiftyResult getFiftyFiftyResult(Question question) {
        return fiftyFiftyCalculator.getPrefixesToDiscard(question);
    }

    @Override
    public FriendsAnswer getFriendsAnswer(Question question, Set<Prefix> rejectedAnswers) {
        return phoneAFriendCalculator.getAnswer(question, rejectedAnswers);
    }

    @Override
    public AudienceAnswer getAudienceAnswer(Question question, Set<Prefix> rejectedAnswers) {
        return askTheAudienceCalculator.getAnswer(question, rejectedAnswers);
    }
}
