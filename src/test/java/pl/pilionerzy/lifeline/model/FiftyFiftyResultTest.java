package pl.pilionerzy.lifeline.model;

import com.google.common.collect.Sets;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;
import static pl.pilionerzy.model.Prefix.*;

public class FiftyFiftyResultTest {

    @Test
    public void shouldImplementToString() {
        assertThat(new FiftyFiftyResult(Sets.newHashSet(B, C)).toString())
                .isEqualTo("Prefixes to discard: [B, C]");
    }

    @Test
    public void shouldThrowExceptionWhenDiscardingInvalidNumberOfPrefixes() {
        assertThatIllegalArgumentException()
                .isThrownBy(() -> new FiftyFiftyResult(Sets.newHashSet(A, B, C)))
                .withMessage("Prefixes to discard must have size 2");
    }
}
