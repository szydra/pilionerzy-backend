package pl.pilionerzy.lifeline.model;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;

@JsonSerialize(using = ToStringSerializer.class)
public class PartialAudienceAnswer {

    private final int votes;

    private PartialAudienceAnswer(int votes) {
        this.votes = votes;
    }

    public static PartialAudienceAnswer withVotes(int votes) {
        return new PartialAudienceAnswer(votes);
    }

    public int getVotes() {
        return votes;
    }

    @Override
    public String toString() {
        return Format.asPercentage(votes);
    }
}
