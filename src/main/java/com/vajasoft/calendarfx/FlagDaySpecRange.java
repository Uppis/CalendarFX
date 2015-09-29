package com.vajasoft.calendarfx;

import com.vajasoft.util.Misc;
import com.vajasoft.util.ValueObjectMarker;
import java.time.DayOfWeek;
import java.time.LocalDate;

/**
 *
 * @author Z705692
 */
public class FlagDaySpecRange extends FlagDaySpec implements ValueObjectMarker {
    private final Integer[] dayRange;
    private final DayOfWeek dayOfWeek;

    public FlagDaySpecRange(int month, Integer[] dayRange, DayOfWeek dayOfWeek, String description) {
        super(month, description);
        this.dayRange = dayRange;
        this.dayOfWeek = dayOfWeek;
    }

    @Override
    public LocalDate getFlagDay(int forYear) {
        LocalDate date = null;
        LocalDate tmp = LocalDate.of(forYear, month, dayRange[0]);
        while (date == null && tmp.getDayOfMonth() <= dayRange[1]) {
            if (tmp.getDayOfWeek().equals(dayOfWeek)) {
                date = tmp;
            } else {
                tmp = tmp.plusDays(1);
            }
        }
        if (date == null) {
            throw new RuntimeException("Invalid configuration: " + this);
        }
        return date;
    }

    @Override
    public String toString() {
        return Misc.buildToString(this);
    }
}
