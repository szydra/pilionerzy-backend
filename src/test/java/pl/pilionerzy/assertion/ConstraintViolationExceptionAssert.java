package pl.pilionerzy.assertion;

import org.assertj.core.api.AbstractThrowableAssert;

import javax.validation.ConstraintViolationException;

import java.util.Objects;

public class ConstraintViolationExceptionAssert
        extends AbstractThrowableAssert<ConstraintViolationExceptionAssert, ConstraintViolationException> {

    public ConstraintViolationExceptionAssert(ConstraintViolationException actual) {
        super(actual, ConstraintViolationExceptionAssert.class);
    }

    public ConstraintViolationExceptionAssert hasViolation(String path, String message) {
        isNotNull();

        if (!matchesViolation(path, message)) {
            failWithMessage("Expected to have violation <%s> in path <%s>", message, path);
        }

        return this;
    }

    private boolean matchesViolation(String path, String message) {
        return actual.getConstraintViolations().stream()
                .anyMatch(violation -> Objects.equals(violation.getMessage(), message)
                        && Objects.equals(violation.getPropertyPath().toString(), path));
    }
}
