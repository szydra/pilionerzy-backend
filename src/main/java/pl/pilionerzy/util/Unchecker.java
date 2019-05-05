package pl.pilionerzy.util;

import java.util.function.Function;

public class Unchecker {

    public static <T, R, E extends Throwable> Function<T, R> uncheck(CheckedFunction<T, R, E> function) {
        return t -> {
            try {
                return function.apply(t);
            } catch (Throwable throwable) {
                throwUnchecked(throwable);
                return null;
            }
        };
    }

    @SuppressWarnings("unchecked")
    private static <E extends Throwable> void throwUnchecked(Throwable e) throws E {
        throw (E) e;
    }

    @FunctionalInterface
    public interface CheckedFunction<T, R, E extends Throwable> {
        R apply(T t) throws E;
    }

}
