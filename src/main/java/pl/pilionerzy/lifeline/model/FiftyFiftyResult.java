package pl.pilionerzy.lifeline.model;

import com.google.common.collect.ImmutableSet;
import pl.pilionerzy.model.Prefix;

import java.util.Collection;

public class FiftyFiftyResult {

    private final Collection<Prefix> prefixesToDiscard;

    public FiftyFiftyResult(Collection<Prefix> prefixesToDiscard) {
        this.prefixesToDiscard = ImmutableSet.copyOf(prefixesToDiscard);
    }

    public Collection<Prefix> getPrefixesToDiscard() {
        return prefixesToDiscard;
    }
}
