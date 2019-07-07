package pl.pilionerzy.lifeline.model;

import com.google.common.collect.ImmutableSortedMap;
import pl.pilionerzy.model.Prefix;

import java.util.Map;

/**
 * Immutable representation of the audience's answers for ask-the-audience lifeline.
 * The results are stored in a sorted map with prefixes and corresponding percentage, e.g.,
 * <ol type="A">
 * <li>13%</li>
 * <li>62%</li>
 * <li>4%</li>
 * <li>21%.</li>
 * </ol>
 */
public class AudienceAnswer {

    private final Map<Prefix, PartialAudienceAnswer> votesChart;

    public AudienceAnswer(Map<Prefix, PartialAudienceAnswer> votesChart) {
        this.votesChart = ImmutableSortedMap.copyOf(votesChart);
    }

    public Map<Prefix, PartialAudienceAnswer> getVotesChart() {
        return votesChart;
    }

    @Override
    public String toString() {
        return votesChart.toString();
    }
}
