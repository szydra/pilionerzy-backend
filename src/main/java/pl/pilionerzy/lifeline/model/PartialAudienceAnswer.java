package pl.pilionerzy.lifeline.model;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;

import java.text.DecimalFormat;

@JsonSerialize(using = ToStringSerializer.class)
public class PartialAudienceAnswer {

    private static final DecimalFormat format;

    static {
        format = new DecimalFormat("0%");
        format.setMultiplier(1);
    }

    private int votes;

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
        return format.format(votes);
    }
}
