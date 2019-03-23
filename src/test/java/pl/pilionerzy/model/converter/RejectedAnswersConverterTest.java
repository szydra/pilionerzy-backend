package pl.pilionerzy.model.converter;

import com.google.common.collect.Lists;
import org.junit.Before;
import org.junit.Test;
import pl.pilionerzy.model.Prefix;

import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;

public class RejectedAnswersConverterTest {

    private RejectedAnswersConverter converter;

    @Before
    public void initConverter() {
        converter = new RejectedAnswersConverter();
    }

    @Test
    public void shouldConvertNullToEmptyCollection() {
        assertThat(converter.convertToEntityAttribute(null))
                .isEmpty();
    }

    @Test
    public void shouldConvertCommaSeparatedPrefixesToNonEmptyCollection() {
        assertThat(converter.convertToEntityAttribute("A,B"))
                .containsExactlyInAnyOrder(Prefix.A, Prefix.B);
    }

    @Test
    public void shouldConvertEmptyCollectionToNull() {
        assertThat(converter.convertToDatabaseColumn(Collections.emptySet()))
                .isNull();
    }

    @Test
    public void shouldConvertNonEmptyCollectionToString() {
        assertThat(converter.convertToDatabaseColumn(Lists.newArrayList(Prefix.C, Prefix.D)))
                .isEqualTo("C,D");
    }
}
