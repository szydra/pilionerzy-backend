package pl.pilionerzy.lifeline.model;

import com.google.common.collect.ImmutableSortedSet;
import pl.pilionerzy.model.Prefix;

import java.util.Collection;

/**
 * Immutable representation of the result of fifty-fifty lifeline application.
 */
public class FiftyFiftyResult {

    private final Collection<Prefix> incorrectPrefixes;

    public FiftyFiftyResult(Collection<Prefix> incorrectPrefixes) {
        if (incorrectPrefixes.size() != 2) {
            throw new IllegalArgumentException("Incorrect prefixes must have size 2");
        }
        this.incorrectPrefixes = ImmutableSortedSet.copyOf(incorrectPrefixes);
    }

    public Collection<Prefix> getIncorrectPrefixes() {
        return incorrectPrefixes;
    }

    @Override
    public String toString() {
        return "Incorrect prefixes: " + incorrectPrefixes;
    }
}
