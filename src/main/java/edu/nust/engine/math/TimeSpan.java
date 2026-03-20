package edu.nust.engine.math;

// represents a given time, with utilities to interconvert between different time units (seconds, milliseconds, etc.)
public class TimeSpan
{
    private final long nanoseconds;

    // use factory methods to create instances
    private TimeSpan(long nanoseconds)
    {
        this.nanoseconds = nanoseconds;
    }

    /* FACTORY */

    public static TimeSpan zero() { return new TimeSpan(0); }

    public static TimeSpan fromSeconds(double s) { return new TimeSpan((long) (s * 1_000_000_000)); }

    public static TimeSpan fromMilliseconds(double ms) { return new TimeSpan((long) (ms * 1_000_000)); }

    public static TimeSpan fromMicroseconds(double us) { return new TimeSpan((long) (us * 1_000)); }

    public static TimeSpan fromNanoseconds(long ns) { return new TimeSpan(ns); }

    /* CONVERSION */

    public double asSeconds() { return nanoseconds / 1_000_000_000.0; }

    public double asMilliseconds() { return nanoseconds / 1_000_000.0; }

    public double asMicroseconds() { return nanoseconds / 1_000.0; }

    public long asNanoseconds() { return nanoseconds; }

    /* ARITHMETIC */

    public TimeSpan add(TimeSpan other) { return new TimeSpan(this.nanoseconds + other.nanoseconds); }

    public TimeSpan subtract(TimeSpan other) { return new TimeSpan(this.nanoseconds - other.nanoseconds); }

    public TimeSpan multiply(double factor) { return new TimeSpan((long) (this.nanoseconds * factor)); }

    public TimeSpan divide(double divisor) { return new TimeSpan((long) (this.nanoseconds / divisor)); }

     /* COMPARISON */

    public boolean isZero() { return this.nanoseconds == 0; }
}
