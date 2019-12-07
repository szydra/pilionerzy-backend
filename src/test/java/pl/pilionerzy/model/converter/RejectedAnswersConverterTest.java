package pl.pilionerzy.model.converter;

import org.junit.Test;

import static com.google.common.collect.Lists.newArrayList;
import static java.util.Collections.emptySet;
import static org.assertj.core.api.Assertions.assertThat;
import static pl.pilionerzy.model.Prefix.*;

public class RejectedAnswersConverterTest {

    private RejectedAnswersConverter converter = new RejectedAnswersConverter();

    @Test
    public void shouldConvertNullToEmptyCollection() {
        assertThat(converter.convertToEntityAttribute(null))
                .isEmpty();
    }

    @Test
    public void shouldConvertCommaSeparatedPrefixesToNonEmptyCollection() {
        assertThat(converter.convertToEntityAttribute("A,B"))
                .containsExactlyInAnyOrder(A, B);
    }

    @Test
    public void shouldConvertEmptyCollectionToNull() {
        assertThat(converter.convertToDatabaseColumn(emptySet()))
                .isNull();
    }

    @Test
    public void shouldConvertNonEmptyCollectionToString() {
        assertThat(converter.convertToDatabaseColumn(newArrayList(C, D)))
                .isEqualTo("C,D");
    }
}
