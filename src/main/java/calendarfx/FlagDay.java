package calendarfx;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.Objects;

/**
 *
 * @author z705692
 */
public class FlagDay implements Serializable, Comparable<FlagDay> {
    private static final long serialVersionUID = 1L;
    private final LocalDate date;
    private final String description;

    public FlagDay(LocalDate date, String description) {
        this.date = date;
        this.description = description;
    }

    public LocalDate getDate() {
        return date;
    }

    public String getDescription() {
        return description;
    }

    @Override
    public int hashCode() {
        return 83 * 3 + Objects.hashCode(this.date);
    }

    @Override
    public boolean equals(Object obj) {
        return obj != null & obj instanceof FlagDay && date.equals(((FlagDay)obj).date);
    }

    @Override
    public int compareTo(FlagDay o) {
        return date.compareTo(o.date);
    }
}
