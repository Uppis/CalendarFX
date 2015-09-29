package com.vajasoft.calendarfx;

import com.vajasoft.util.Misc;
import com.vajasoft.util.ValueObjectMarker;
import java.time.LocalDate;

/**
 *
 * @author Z705692
 */
public class FlagDaySpecFixed extends FlagDaySpec implements ValueObjectMarker {
    private final Integer dayOfMonth;

    public FlagDaySpecFixed(int month, Integer dayOfMonth, String description) {
        super(month, description);
        this.dayOfMonth = dayOfMonth;
    }

    @Override
    public LocalDate getFlagDay(int forYear) {
        return LocalDate.of(forYear, month, dayOfMonth);
    }

    @Override
    public String toString() {
        return Misc.buildToString(this);
    }
}
