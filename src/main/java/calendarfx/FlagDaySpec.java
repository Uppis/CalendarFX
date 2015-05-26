package calendarfx;

import com.nordea.common.util.Misc;
import com.nordea.common.util.ValueObjectMarker;
import java.time.LocalDate;

/**
 *
 * @author z705692
 */
public abstract class FlagDaySpec implements ValueObjectMarker {
    protected final int month;
    protected final String description;

    public FlagDaySpec(int month, String description) {
        this.month = month;
        this.description = description;
    }

    public abstract LocalDate getFlagDay(int forYear);

    public String getDescription() {
        return description;
    }

    @Override
    public String toString() {
        return Misc.buildToString(this);
    }
}
