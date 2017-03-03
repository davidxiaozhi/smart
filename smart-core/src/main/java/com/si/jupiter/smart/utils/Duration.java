package com.si.jupiter.smart.utils;

import com.google.common.base.Preconditions;
import com.google.common.primitives.Doubles;

import java.util.Locale;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.util.concurrent.TimeUnit.*;

/**
 * 时间窗口工具类
 * Version    : １.0
 * Author     : lizhipeng@jd.com
 * Owner      : si-infra@jd.com
 * All Rights Reserved by jd.com 201７
 * create     :  17-2-13 下午5:56
 * Last Update:  17-2-13 下午5:56
 */
public class Duration implements Comparable<Duration>{
    private static final Pattern PATTERN = Pattern.compile("^\\s*(\\d+(?:\\.\\d+)?)\\s*([a-zA-Z]+)\\s*$");

    public static Duration nanosSince(long start)
    {
        long end = System.nanoTime();

        long value = end - start;
        double millis = value * millisPerTimeUnit(NANOSECONDS);
        return new Duration(millis, MILLISECONDS).convertToMostSuccinctTimeUnit();
    }

    public static Duration succinctNanos(long nanos)
    {
        return succinctDuration(nanos, NANOSECONDS);
    }

    public static Duration succinctDuration(double value, TimeUnit unit)
    {
        return new Duration(value, unit).convertToMostSuccinctTimeUnit();
    }

    private final double value;
    private final TimeUnit unit;

    public Duration(double value, TimeUnit unit)
    {
        Preconditions.checkArgument(!Double.isInfinite(value), "value is infinite");
        Preconditions.checkArgument(!Double.isNaN(value), "value is not a number");
        Preconditions.checkArgument(value >= 0, "value is negative");
        Preconditions.checkNotNull(unit, "unit is null");

        this.value = value;
        this.unit = unit;
    }

    public long toMillis()
    {
        return roundTo(MILLISECONDS);
    }

    public double getValue()
    {
        return value;
    }

    public TimeUnit getUnit()
    {
        return unit;
    }

    public double getValue(TimeUnit timeUnit)
    {
        Preconditions.checkNotNull(timeUnit, "timeUnit is null");
        return value * (millisPerTimeUnit(this.unit) * 1.0 / millisPerTimeUnit(timeUnit));
    }

    public long roundTo(TimeUnit timeUnit)
    {
        Preconditions.checkNotNull(timeUnit, "timeUnit is null");
        double rounded = Math.floor(getValue(timeUnit) + 0.5d);
        Preconditions.checkArgument(rounded <= Long.MAX_VALUE,
                "size is too large to be represented in requested unit as a long");
        return (long) rounded;
    }

    public Duration convertTo(TimeUnit timeUnit)
    {
        Preconditions.checkNotNull(timeUnit, "timeUnit is null");
        return new Duration(getValue(timeUnit), timeUnit);
    }

    public Duration convertToMostSuccinctTimeUnit()
    {
        TimeUnit unitToUse = NANOSECONDS;
        for (TimeUnit unitToTest : TimeUnit.values()) {
            // since time units are powers of ten, we can get rounding errors here, so fuzzy match
            if (getValue(unitToTest) > 0.9999) {
                unitToUse = unitToTest;
            }
            else {
                break;
            }
        }
        return convertTo(unitToUse);
    }


    @Override
    public String toString()
    {
        return toString(unit);
    }

    public String toString(TimeUnit timeUnit)
    {
        Preconditions.checkNotNull(timeUnit, "timeUnit is null");
        double magnitude = getValue(timeUnit);
        String timeUnitAbbreviation = timeUnitToString(timeUnit);
        return String.format(Locale.ENGLISH, "%.2f%s", magnitude, timeUnitAbbreviation);
    }

    public static Duration valueOf(String duration)
            throws IllegalArgumentException
    {
        Preconditions.checkNotNull(duration, "duration is null");
        Preconditions.checkArgument(!duration.isEmpty(), "duration is empty");

        Matcher matcher = PATTERN.matcher(duration);
        if (!matcher.matches()) {
            throw new IllegalArgumentException("duration is not a valid data duration string: " + duration);
        }

        double value = Double.parseDouble(matcher.group(1));
        String unitString = matcher.group(2);

        TimeUnit timeUnit = valueOfTimeUnit(unitString);
        return new Duration(value, timeUnit);
    }


    @Override
    public int compareTo(Duration o)
    {
        return Double.compare(getValue(MILLISECONDS), o.getValue(MILLISECONDS));
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        Duration duration = (Duration) o;

        return compareTo(duration) == 0;
    }

    @Override
    public int hashCode()
    {
        double value = getValue(MILLISECONDS);
        return Doubles.hashCode(value);
    }


    public static TimeUnit valueOfTimeUnit(String timeUnitString)
    {
        Preconditions.checkNotNull(timeUnitString, "timeUnitString is null");
        switch (timeUnitString) {
            case "ns":
                return NANOSECONDS;
            case "us":
                return MICROSECONDS;
            case "ms":
                return MILLISECONDS;
            case "s":
                return SECONDS;
            case "m":
                return MINUTES;
            case "h":
                return HOURS;
            case "d":
                return DAYS;
            default:
                throw new IllegalArgumentException("Unknown time unit: " + timeUnitString);
        }
    }

    public static String timeUnitToString(TimeUnit timeUnit)
    {
        Preconditions.checkNotNull(timeUnit, "timeUnit is null");
        switch (timeUnit) {
            case NANOSECONDS:
                return "ns";
            case MICROSECONDS:
                return "us";
            case MILLISECONDS:
                return "ms";
            case SECONDS:
                return "s";
            case MINUTES:
                return "m";
            case HOURS:
                return "h";
            case DAYS:
                return "d";
            default:
                throw new IllegalArgumentException("Unsupported time unit " + timeUnit);
        }
    }

    private static double millisPerTimeUnit(TimeUnit timeUnit)
    {
        switch (timeUnit) {
            case NANOSECONDS:
                return 1.0 / 1000000.0;
            case MICROSECONDS:
                return 1.0 / 1000.0;
            case MILLISECONDS:
                return 1;
            case SECONDS:
                return 1000;
            case MINUTES:
                return 1000 * 60;
            case HOURS:
                return 1000 * 60 * 60;
            case DAYS:
                return 1000 * 60 * 60 * 24;
            default:
                throw new IllegalArgumentException("Unsupported time unit " + timeUnit);
        }
    }
}
