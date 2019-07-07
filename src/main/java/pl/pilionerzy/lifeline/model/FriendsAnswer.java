package pl.pilionerzy.lifeline.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import pl.pilionerzy.model.Prefix;

/**
 * Immutable representation of friend's answer for phone-a-friend lifeline.
 */
public class FriendsAnswer {

    private final Prefix prefix;
    private final int wisdom;

    public FriendsAnswer(Prefix prefix, int wisdom) {
        this.prefix = prefix;
        this.wisdom = wisdom;
    }

    @JsonProperty
    public String getWisdom() {
        return Format.asPercentage(wisdom);
    }

    public Prefix getPrefix() {
        return prefix;
    }

    @Override
    public String toString() {
        return "Prefix: " + prefix + ", wisdom: " + wisdom;
    }
}
