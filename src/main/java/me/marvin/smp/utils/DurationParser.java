package me.marvin.smp.utils;

import org.jetbrains.annotations.NotNull;

import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.time.temporal.Temporal;
import java.util.List;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * A simple duration parser what parses durations using regex.
 */
public interface DurationParser {
    Pattern MATCHER_PATTERN = Pattern.compile("\\d+\\D+");
    Pattern DATE_REGEX = Pattern.compile("(?<=\\D)(?=\\d)|(?<=\\d)(?=\\D)");
    List<ChronoUnit> UNITS = List.of(ChronoUnit.YEARS, ChronoUnit.MONTHS, ChronoUnit.WEEKS, ChronoUnit.DAYS, ChronoUnit.HOURS, ChronoUnit.MINUTES, ChronoUnit.SECONDS);

    /**
     * Parses the given duration string, and adds it to the given {@link Temporal}.
     *
     * @param temporal the temporal
     * @param input the string
     * @return the parsed time
     */
    @NotNull
    static <R extends Temporal> R parse(@NotNull R temporal, @NotNull String input) {
        return ChronoUnit.MILLIS.addTo(temporal, parse(input));
    }

    /**
     * Parses the given duration string, and adds it to the given {@link Temporal}.
     *
     * @param input the string
     * @return the parsed time
     */
    static long parse(@NotNull String input) {
        long amount = 0;
        Matcher matcher = MATCHER_PATTERN.matcher(input);

        while (matcher.find()) {
            String group = matcher.group();
            String[] split = DATE_REGEX.split(group);

            String type = split[1];
            long value = Long.parseLong(split[0]);
            Duration duration = chronoUnit(type).getDuration();

            amount += Math.multiplyExact(duration.toMillis(), value);
        }

        return amount;
    }

    /**
     * Converts a 64-bit number into a simple time format.
     *
     * @param millis the number
     * @return the formatted value
     */
    @NotNull
    static String parse(long millis) {
        if (millis == -1) {
            return "-";
        }

        StringBuilder sb = new StringBuilder();
        for (ChronoUnit unit : UNITS) {
            long unitValue = millis(unit);
            long amount = millis / unitValue;
            millis %= unitValue;

            if (amount > 0) {
                sb.append(amount).append(chronoUnit(unit));
            }
        }

        String result = sb.toString();
        return !result.isEmpty() ? result : "-";
    }

    /**
     * Rounds the given 64-bit number to a date.
     *
     * @param millis the number
     * @return the rounded value
     */
    @NotNull
    static Tuple<Long, ChronoUnit> round(long millis) {
        if (millis == -1) {
            return Tuple.tuple(-1L, null);
        }

        for (ChronoUnit unit : UNITS) {
            long amount = millis / millis(unit);
            if (amount > 0) {
                return Tuple.tuple(amount, unit);
            }
        }

        return Tuple.tuple(millis, ChronoUnit.MILLIS);
    }

    private static long millis(@NotNull ChronoUnit unit) {
        return unit.getDuration().toMillis();
    }

    @NotNull
    private static ChronoUnit chronoUnit(String type) {
        return switch (type) {
            case "y" -> ChronoUnit.YEARS;
            case "M" -> ChronoUnit.MONTHS;
            case "w" -> ChronoUnit.WEEKS;
            case "d" -> ChronoUnit.DAYS;
            case "h" -> ChronoUnit.HOURS;
            case "m" -> ChronoUnit.MINUTES;
            case "s" -> ChronoUnit.SECONDS;
            default -> throw new IllegalArgumentException("invalid character: '%s'".formatted(type));
        };
    }

    @NotNull
    private static String chronoUnit(ChronoUnit unit) {
        return switch (unit) {
            case YEARS -> "y";
            case MONTHS -> "M";
            case WEEKS -> "w";
            case DAYS -> "d";
            case HOURS -> "h";
            case MINUTES -> "m";
            case SECONDS -> "s";
            default -> throw new IllegalArgumentException("invalid unit: 'ChronoUnit.%s'".formatted(unit));
        };
    }
}
