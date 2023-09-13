package me.marvin.smp.utils;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;
import java.util.function.Consumer;

/**
 * Represents an operation that accepts a single input argument which may throw an exception
 * and returns no result. Unlike most other functional interfaces, {@code ThrowableConsumer}
 * is expected to operate via side-effects.
 *
 * <p>This is a <a href="package-summary.html">functional interface</a>
 * whose functional method is {@link #accept(Object)}.
 *
 * @param <T> the type of the input to the operation
 * @param <E> the type of the throwable which is may thrown while operation
 *
 * @see Consumer
 */
@FunctionalInterface
public interface ThrowableConsumer<T, E extends Throwable> {
    /**
     * Performs this operation on the given argument.
     * While operation, it may throw a {@code Throwable}.
     *
     * @param t the function argument
     * @throws E the type of the throwable
     */
    void accept(T t) throws E;

    /**
     * Returns a composed {@code ThrowableConsumer} that performs, in sequence, this
     * operation followed by the {@code after} operation. If performing either
     * operation throws an exception, it is relayed to the caller of the
     * composed operation.  If performing this operation throws an exception,
     * the {@code after} operation will not be performed.
     *
     * @param after the operation to perform after this operation
     * @return a composed {@code ThrowableConsumer} that performs in sequence this
     * operation followed by the {@code after} operation
     * @throws NullPointerException if {@code after} is null
     */
    @NotNull
    default ThrowableConsumer<T, E> andThen(@NotNull ThrowableConsumer<? super T, E> after) {
        Objects.requireNonNull(after);
        return (T t) -> {
            accept(t);
            after.accept(t);
        };
    }
}