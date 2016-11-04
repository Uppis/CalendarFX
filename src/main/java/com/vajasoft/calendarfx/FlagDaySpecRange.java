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
    private final int startDay;
    private final DayOfWeek dayOfWeek;

    public FlagDaySpecRange(int startMonth, int startDay, DayOfWeek dayOfWeek, String description) {
        super(startMonth, description);
        this.startDay = startDay;
        this.dayOfWeek = dayOfWeek;
    }

    @Override
    public LocalDate getFlagDay(int forYear) {
        int count = 7;  // The dayOfWeek should be found within a week ...
        LocalDate date = null;
        LocalDate tmp = LocalDate.of(forYear, month, startDay);
        while (date == null && count-- > 0) {
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
