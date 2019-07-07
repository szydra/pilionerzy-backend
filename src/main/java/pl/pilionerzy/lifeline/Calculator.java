package pl.pilionerzy.lifeline;

import pl.pilionerzy.lifeline.model.AudienceAnswer;
import pl.pilionerzy.lifeline.model.FiftyFiftyResult;
import pl.pilionerzy.lifeline.model.FriendsAnswer;
import pl.pilionerzy.model.Prefix;
import pl.pilionerzy.model.Question;

import java.util.Set;

public interface Calculator {

    FiftyFiftyResult getFiftyFiftyResult(Question question);

    FriendsAnswer getFriendsAnswer(Question question, Set<Prefix> rejectedAnswers);

    AudienceAnswer getAudienceAnswer(Question question, Set<Prefix> rejectedAnswers);

}
