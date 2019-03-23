package pl.pilionerzy.model.converter;

import pl.pilionerzy.model.Prefix;

import javax.persistence.AttributeConverter;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.stream.Collectors;

public class RejectedAnswersConverter implements AttributeConverter<Collection<Prefix>, String> {

    @Override
    public String convertToDatabaseColumn(Collection<Prefix> prefixes) {
        if (prefixes == null || prefixes.isEmpty()) {
            return null;
        } else {
            return prefixes.stream()
                    .map(Prefix::toString)
                    .collect(Collectors.joining(","));
        }
    }

    @Override
    public Collection<Prefix> convertToEntityAttribute(String s) {
        if (s == null || s.isEmpty()) {
            return Collections.emptySet();
        } else {
            return Arrays.stream(s.split(","))
                    .map(Prefix::valueOf)
                    .collect(Collectors.toSet());
        }
    }
}
