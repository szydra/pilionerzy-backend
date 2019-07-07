package pl.pilionerzy.lifeline.model;

import com.google.common.collect.ImmutableSortedSet;
import pl.pilionerzy.model.Prefix;

import java.util.Collection;

/**
 * Immutable representation of the result of fifty-fifty lifeline application.
 */
public class FiftyFiftyResult {

    private final Collection<Prefix> prefixesToDiscard;

    public FiftyFiftyResult(Collection<Prefix> prefixesToDiscard) {
        if (prefixesToDiscard.size() != 2) {
            throw new IllegalArgumentException("Prefixes to discard must have size 2");
        }
        this.prefixesToDiscard = ImmutableSortedSet.copyOf(prefixesToDiscard);
    }

    public Collection<Prefix> getPrefixesToDiscard() {
        return prefixesToDiscard;
    }

    @Override
    public String toString() {
        return "Prefixes to discard: " + prefixesToDiscard;
    }
}
