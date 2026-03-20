package edu.nust.engine.math;

/// Represents a given time, with utilities to interconvert between different time units (seconds, milliseconds, etc.)
public class TimeSpan
{
    private final long nanoseconds;

    /// Use [TimeSpan#fromNanoseconds(long)] or other factory methods instead.
    ///
    /// @see TimeSpan#fromNanoseconds(long)
    /// @see TimeSpan#fromMicroseconds(double)
    /// @see TimeSpan#fromMilliseconds(double)
    /// @see TimeSpan#fromSeconds(double)
    /// @see TimeSpan#fromMinutes(double)
    /// @see TimeSpan#fromHours(double)
    /// @see TimeSpan#fromDays(double)
    private TimeSpan(long nanoseconds) { this.nanoseconds = nanoseconds; }

    @Override
    public String toString()
    {
        return String.format("TimeSpan(%.3f milliseconds)", asMilliseconds());
    }

    /* FACTORY */

    public static TimeSpan zero() { return new TimeSpan(0); }

    public static TimeSpan fromNanoseconds(long ns) { return new TimeSpan(ns); }

    public static TimeSpan fromMicroseconds(double us) { return fromNanoseconds((long) (us * 1_000)); }

    public static TimeSpan fromMilliseconds(double ms) { return fromMicroseconds(ms * 1_000); }

    public static TimeSpan fromSeconds(double s) { return fromMilliseconds(s * 1_000); }

    public static TimeSpan fromMinutes(double m) { return fromSeconds(m * 60); }

    public static TimeSpan fromHours(double h) { return fromMinutes(h * 60); }

    public static TimeSpan fromDays(double d) { return fromHours(d * 24); }

    /* CONVERSION */

    public long asNanoseconds() { return nanoseconds; }

    public double asMicroseconds() { return (double) asNanoseconds() / 1_000; }

    public double asMilliseconds() { return asMicroseconds() / 1_000; }

    public double asSeconds() { return asMilliseconds() / 1_000; }

    public double asMinutes() { return asSeconds() / 60; }

    public double asHours() { return asMinutes() / 60; }

    public double asDays() { return asHours() / 24; }

    /* OPERATORS NON MUTATING (COPY) */

    public TimeSpan add(TimeSpan other) { return new TimeSpan(this.nanoseconds + other.nanoseconds); }

    public TimeSpan subtract(TimeSpan other) { return new TimeSpan(this.nanoseconds - other.nanoseconds); }

    public TimeSpan multiply(double factor) { return new TimeSpan((long) (this.nanoseconds * factor)); }

    public TimeSpan divide(double divisor) { return new TimeSpan((long) (this.nanoseconds / divisor)); }

    public TimeSpan clamp(TimeSpan min, TimeSpan max)
    {
        return new TimeSpan(Math.max(min.nanoseconds, Math.min(max.nanoseconds, this.nanoseconds)));
    }

    public TimeSpan min(TimeSpan other) { return new TimeSpan(Math.min(this.nanoseconds, other.nanoseconds)); }

    public TimeSpan max(TimeSpan other) { return new TimeSpan(Math.max(this.nanoseconds, other.nanoseconds)); }

    /* OPERATORS SELF */

    // None since `final`

    /* OPERATORS STATICS */

    public static TimeSpan add(TimeSpan a, TimeSpan b) { return new TimeSpan(a.nanoseconds + b.nanoseconds); }

    public static TimeSpan subtract(TimeSpan a, TimeSpan b) { return new TimeSpan(a.nanoseconds - b.nanoseconds); }

    public static TimeSpan multiply(TimeSpan time, double factor) { return new TimeSpan((long) (time.nanoseconds * factor)); }

    public static TimeSpan divide(TimeSpan time, double divisor) { return new TimeSpan((long) (time.nanoseconds / divisor)); }

    public static TimeSpan clamp(TimeSpan time, TimeSpan min, TimeSpan max)
    {
        return new TimeSpan(Math.max(min.nanoseconds, Math.min(max.nanoseconds, time.nanoseconds)));
    }

    public static TimeSpan min(TimeSpan a, TimeSpan b) { return new TimeSpan(Math.min(a.nanoseconds, b.nanoseconds)); }

    public static TimeSpan max(TimeSpan a, TimeSpan b) { return new TimeSpan(Math.max(a.nanoseconds, b.nanoseconds)); }

    /* COMPARISON */

    public boolean isZero() { return this.nanoseconds == 0; }
}
