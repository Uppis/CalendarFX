package calendarfx;

import com.nordea.common.util.Misc;
import com.nordea.common.util.ValueObjectMarker;
import java.time.DayOfWeek;
import java.time.LocalDate;

/**
 *
 * @author Z705692
 */
public class FlagDaySpecOrder extends FlagDaySpec implements ValueObjectMarker {
    private final int orderOfDayOfWeek;
    private final DayOfWeek dayOfWeek;

    public FlagDaySpecOrder(int month, int orderOfDayOfWeek, DayOfWeek dayOfWeek, String description) {
        super(month, description);
        this.orderOfDayOfWeek = orderOfDayOfWeek;
        this.dayOfWeek = dayOfWeek;
    }

    @Override
    public LocalDate getFlagDay(int forYear) {
        int order = orderOfDayOfWeek;
        LocalDate date = null;
        LocalDate tmp = LocalDate.of(forYear, month, 1);
        while (date == null && tmp.getMonthValue() == month) {
            if (tmp.getDayOfWeek().equals(dayOfWeek) && --order == 0) {
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
