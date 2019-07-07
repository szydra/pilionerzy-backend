package pl.pilionerzy.lifeline;

import org.springframework.stereotype.Component;
import pl.pilionerzy.lifeline.model.AudienceAnswer;
import pl.pilionerzy.lifeline.model.FiftyFiftyResult;
import pl.pilionerzy.lifeline.model.FriendsAnswer;
import pl.pilionerzy.model.Prefix;
import pl.pilionerzy.model.Question;

import java.util.Set;

@Component
class RandomizedCalculator implements Calculator {

    private FiftyFiftyCalculator fiftyFiftyCalculator;
    private PhoneAFriendCalculator phoneAFriendCalculator;
    private AskTheAudienceCalculator askTheAudienceCalculator;

    RandomizedCalculator(FiftyFiftyCalculator fiftyFiftyCalculator,
                         PhoneAFriendCalculator phoneAFriendCalculator,
                         AskTheAudienceCalculator askTheAudienceCalculator) {
        this.fiftyFiftyCalculator = fiftyFiftyCalculator;
        this.phoneAFriendCalculator = phoneAFriendCalculator;
        this.askTheAudienceCalculator = askTheAudienceCalculator;
    }

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
