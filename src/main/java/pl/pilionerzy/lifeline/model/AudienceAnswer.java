package pl.pilionerzy.lifeline.model;

import pl.pilionerzy.model.Prefix;

import java.util.Map;

public class AudienceAnswer {

    private final Map<Prefix, PartialAudienceAnswer> votesChart;

    public AudienceAnswer(Map<Prefix, PartialAudienceAnswer> votesChart) {
        this.votesChart = votesChart;
    }

    public Map<Prefix, PartialAudienceAnswer> getVotesChart() {
        return votesChart;
    }
}
