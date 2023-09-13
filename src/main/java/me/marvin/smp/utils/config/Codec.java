package me.marvin.smp.utils.config;

import org.jetbrains.annotations.NotNull;

import java.util.function.Function;

/**
 * Represents a simple configuration field codec.
 *
 * @param deserializer the deserializer
 * @param serializer the serializer
 * @param <O> the origin type
 * @param <T> the target type
 */
public record Codec<O, T>(@NotNull Function<O, T> deserializer, @NotNull Function<T, O> serializer) {
    private static final Codec<?, ?> IDENTITY_CODEC = new Codec<>(o -> o, t -> t);

    /**
     * Deserializes the given {@link O origin} object into a {@link T target} object.
     *
     * @param o the {@link O origin} object
     * @return a {@link T target} object
     */
    @NotNull
    public T deserialize(@NotNull O o) {
        return deserializer.apply(o);
    }

    /**
     * Serializes the given {@link T target} object into an {@link O origin} object.
     *
     * @param t the {@link T target} object
     * @return an {@link O origin} object
     */
    @NotNull
    public O serialize(@NotNull T t) {
        return serializer.apply(t);
    }

    /**
     * Returns a codec what always returns the given argument.
     *
     * @return an identity codec
     */
    @NotNull
    public static Codec<?, ?> identity() {
        return IDENTITY_CODEC;
    }
}