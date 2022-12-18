package pl.pilionerzy.lifeline.model;

import com.google.common.collect.Sets;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;
import static pl.pilionerzy.model.Prefix.*;

class FiftyFiftyResultTest {

    @Test
    void shouldImplementToString() {
        assertThat(new FiftyFiftyResult(Sets.newHashSet(B, C)).toString())
                .isEqualTo("Incorrect prefixes: [B, C]");
    }

    @Test
    void shouldThrowExceptionWhenDiscardingInvalidNumberOfPrefixes() {
        assertThatIllegalArgumentException()
                .isThrownBy(() -> new FiftyFiftyResult(Sets.newHashSet(A, B, C)))
                .withMessage("Incorrect prefixes must have size 2");
    }
}
