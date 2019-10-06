package pl.pilionerzy.assertion;

import javax.validation.ConstraintViolationException;

public class Assertions extends org.assertj.core.api.Assertions {

    public static ConstraintViolationExceptionAssert assertThat(ConstraintViolationException actual) {
        return new ConstraintViolationExceptionAssert(actual);
    }
}
