package pl.pilionerzy.model.converter;

import org.junit.jupiter.api.Test;

import static com.google.common.collect.Lists.newArrayList;
import static java.util.Collections.emptySet;
import static org.assertj.core.api.Assertions.assertThat;
import static pl.pilionerzy.model.Prefix.*;

class RejectedAnswersConverterTest {

    private final RejectedAnswersConverter converter = new RejectedAnswersConverter();

    @Test
    void shouldConvertNullToEmptyCollection() {
        assertThat(converter.convertToEntityAttribute(null))
                .isEmpty();
    }

    @Test
    void shouldConvertCommaSeparatedPrefixesToNonEmptyCollection() {
        assertThat(converter.convertToEntityAttribute("A,B"))
                .containsExactlyInAnyOrder(A, B);
    }

    @Test
    void shouldConvertEmptyCollectionToNull() {
        assertThat(converter.convertToDatabaseColumn(emptySet()))
                .isNull();
    }

    @Test
    void shouldConvertNonEmptyCollectionToString() {
        assertThat(converter.convertToDatabaseColumn(newArrayList(C, D)))
                .isEqualTo("C,D");
    }
}
