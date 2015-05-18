package calendarfx;

import java.net.URL;
import java.text.DateFormatSymbols;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.ResourceBundle;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;

public class FXMLController implements Initializable {
    private static final int NBR_OF_COLUMNS = 7;
    private static final int NBR_OF_ROWS = 6;
    private final GregorianCalendar today = new GregorianCalendar();
    private final int firstDayOfWeek = today.getFirstDayOfWeek();
    private GregorianCalendar month;
    private final DateFormatSymbols symbols = new java.text.DateFormatSymbols();
    private final String[] monthNames = symbols.getMonths();
    private final String[] weekdayNames = symbols.getShortWeekdays();
    private final Image flag = new Image("/img/suomi.gif");
    private boolean flagOn = true;

    @FXML
    Label fldMonth;
    @FXML
    Button cmdPrevYear;
    @FXML
    Button cmdFirstMonth;
    @FXML
    Button cmdPrevMonth;
    @FXML
    Button cmdToday;
    @FXML
    Button cmdNextMonth;
    @FXML
    Button cmdLastMonth;
    @FXML
    Button cmdNextYear;
    @FXML
    GridPane pnlWeekdays;
    @FXML
    GridPane pnlWeeks;
    @FXML
    GridPane pnlDays;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        addLabels(pnlWeekdays, NBR_OF_COLUMNS, 1);
        for (int i = 1; i < weekdayNames.length; i++) {
            int wkDayIx = weekdayIndex(i);
            Label weekday = (Label)pnlWeekdays.getChildren().get(wkDayIx);
            weekday.setText(weekdayNames[i]);
            if (i == Calendar.SUNDAY) {
                weekday.getStyleClass().add("sunday-label");
            } else {
                weekday.getStyleClass().add("weekday-label");
            }
        }
        addLabels(pnlWeeks, 1, NBR_OF_ROWS);
        addLabels(pnlDays, NBR_OF_COLUMNS, NBR_OF_ROWS);
        moveTo(today);
    }

    private void addLabels(GridPane to, int columnCount, int rowCount) {
        for (int j = 0; j < rowCount; j++) {
            for (int i = 0; i < columnCount; i++) {
                Label lbl = new Label();
                lbl.setAlignment(Pos.CENTER);
                lbl.setContentDisplay(ContentDisplay.RIGHT);
                lbl.setGraphicTextGap(0);
//            lbl.setFont(to.getFont());
//            lbl.setForeground(to.getForeground());
//            lbl.setHorizontalTextPosition(JLabel.CENTER);
//            lbl.setVerticalTextPosition(JLabel.CENTER);
//            lbl.setText(" ");
                to.add(lbl, i, j);
            }
        }
    }

    private void maybeShowToday(GregorianCalendar currentMonth) {
        Label lbl = (Label)pnlDays.getChildren().get(gridIndex(today));
        // Reset possibly old "today"
        // If today is in current month, set "today"
        ObservableList<String> styleClass = lbl.getStyleClass();
        if (today.get(Calendar.YEAR) == currentMonth.get(Calendar.YEAR) && today.get(Calendar.MONTH) == currentMonth.get(Calendar.MONTH)) {
            styleClass.add("today-label");
        } else {
            styleClass.remove("today-label");
        }
    }

    private void moveTo(GregorianCalendar date) {
        month = (GregorianCalendar)date.clone();
        month.set(Calendar.DATE, 1);
        setDate(month);
    }

    private void moveToBOY() {
        month.set(Calendar.MONTH, Calendar.JANUARY);
        setDate(month);
    }

    private void moveToEOY() {
        month.set(Calendar.MONTH, Calendar.DECEMBER);
        setDate(month);
    }

    private void moveDate(int unit, int amount) {
        month.add(unit, amount);
        setDate(month);
    }

    private void setDate(GregorianCalendar date) {
        fldMonth.setText(monthNames[date.get(Calendar.MONTH)] + " " + date.get(Calendar.YEAR));
        maybeShowToday(date);
        GregorianCalendar d = (GregorianCalendar)date.clone();
        d.add(Calendar.DATE, -gridIndex(date) - 1);
        for (int row = 0; row < NBR_OF_ROWS; row++) {
            boolean daysInRow = false;
            for (int col = 0; col < NBR_OF_COLUMNS; col++) {
                int ix = row * NBR_OF_COLUMNS + col;
                Label lbl = (Label)pnlDays.getChildren().get(ix);
                lbl.setGraphic(null);
                d.add(Calendar.DATE, 1);
                if (d.get(Calendar.MONTH) == date.get(Calendar.MONTH)) {
                    lbl.setText(String.valueOf(d.get(Calendar.DATE)));
                    if (isFeastDay(d)) {
                        lbl.getStyleClass().add("sunday-label");
                    } else {
                        lbl.getStyleClass().remove("sunday-label");
                    }
                    if (flagOn && isFlagDay(d)) {
                        lbl.setGraphic(new ImageView(flag));
                    }
                    daysInRow = true;
                } else {
                    lbl.setText("");
                }
            }
            Label weekLbl = (Label)pnlWeeks.getChildren().get(row);
            if (daysInRow) {
                weekLbl.setText(String.valueOf(d.get(Calendar.WEEK_OF_YEAR)));
            } else {
                weekLbl.setText("");
            }
        }
    }

    private int weekdayIndex(int wd) {
        return (wd + (NBR_OF_COLUMNS - firstDayOfWeek)) % NBR_OF_COLUMNS;
    }

    private int gridIndex(GregorianCalendar day) {
        int d = (day.get(Calendar.DATE) - 1) % 7;
        int dow = ((7 + day.get(Calendar.DAY_OF_WEEK) - 1 - d) % 7) + 1;
        int start = weekdayIndex(dow);
        return start + day.get(Calendar.DATE) - 1;
    }

    @FXML
    private void onCmdPrevYearAction(ActionEvent e) {
        moveDate(Calendar.YEAR, -1);
    }

    @FXML
    private void onCmdFirstMonthAction(ActionEvent e) {
        moveToBOY();
    }

    @FXML
    private void onCmdPrevMonthAction(ActionEvent e) {
        moveDate(Calendar.MONTH, -1);
    }

    @FXML
    private void onCmdTodayAction(ActionEvent e) {
        moveTo(today);
    }

    @FXML
    private void onCmdNextMonthAction(ActionEvent e) {
        moveDate(Calendar.MONTH, 1);
    }

    @FXML
    private void onCmdLastMonthAction(ActionEvent e) {
        moveToEOY();
    }

    @FXML
    private void onCmdNextYearAction(ActionEvent e) {
        moveDate(Calendar.YEAR, 1);
    }

    private static boolean isFeastDay(Calendar date) {
        boolean ret = false;
        int weekday = date.get(Calendar.DAY_OF_WEEK);

        if (weekday == Calendar.SUNDAY) {
            ret = true;
        } else {
            int day = date.get(Calendar.DATE);
            int month = date.get(Calendar.MONTH); // Zero based !
            int year = date.get(Calendar.YEAR);
            boolean since_1992 = year >= 1992;

            if (month == Calendar.JANUARY
                    && (day == 1 || // uudenvuoden päivä
                    (day == 6 && since_1992))) // loppiainen
            {
                ret = true;
            } else if (month == Calendar.MAY
                    && day == 1) // vappu
            {
                ret = true;
            } else if (month == Calendar.JUNE) { // juhannusaatto ?
                ret = day == getMidsummerDay(year);
            } else if (month == Calendar.DECEMBER
                    && (day == 6 || // itsenäisyyspäivä
                    (day >= 25 && day <= 26))) // Joulun pyhät
            {
                ret = true;
            } else { // Pääsiäinen ja helatorstai
                Calendar eastern = getEastern(year);
                int d = diff(eastern, date);
                if (d == -2 || d == 0 || d == 1) {
                    ret = true;
                } else if (d == 39 && since_1992) // helatorstai
                {
                    ret = true;
                }
            }
        }
        return ret;
    }

    private static int getMidsummerDay(int year) {
        GregorianCalendar tmp = new GregorianCalendar(year, Calendar.JUNE, 20);
        return 27 - tmp.get(Calendar.DAY_OF_WEEK);
    }

    private static Calendar getEastern(int year) {
        int e_month = Calendar.MARCH;
        int tmp1 = (19 * (year % 19) + 24) % 30;
        int tmp2 = (2 * (year % 4) + 4 * (year % 7) + 6 * tmp1 + 5) % 7;
        int e_day = 22 + tmp1 + tmp2;

        if (e_day > 31) {
            e_month = Calendar.APRIL;
            e_day = tmp1 + tmp2 - 9;
            if (e_day == 26) {
                e_day = 19;
            } else if (e_day == 25 && tmp1 == 28 && (year % 19) > 10) {
                e_day = 18;
            }
        }
        return new GregorianCalendar(year, e_month, e_day);
    }

    private static int diff(Calendar day1, Calendar day2) {
        boolean before = day1.before(day2);
        int d1 = before ? day1.get(Calendar.DAY_OF_YEAR)
                : day2.get(Calendar.DAY_OF_YEAR);
        int d2 = before ? day2.get(Calendar.DAY_OF_YEAR)
                : day1.get(Calendar.DAY_OF_YEAR);
        int y1 = before ? day1.get(Calendar.YEAR) : day2.get(Calendar.YEAR);
        int y2 = before ? day2.get(Calendar.YEAR) : day1.get(Calendar.YEAR);
        while (y1 < y2) {
            d2 += isLeapYear(y1++) ? 366 : 365;
        }
        return (d2 - d1) * (before ? 1 : -1);
    }

    private static boolean isLeapYear(int y) {
        return (y % 400 == 0) || ((y % 4 == 0) && (y % 100 != 0));
    }

    private static boolean isFlagDay(Calendar date) {
        boolean ret = false;
        int day = date.get(Calendar.DATE);
        int month = date.get(Calendar.MONTH); // Zero based !
        int year = date.get(Calendar.YEAR);
        int dow = date.get(Calendar.DAY_OF_WEEK);
        int dowim = date.get(Calendar.DAY_OF_WEEK_IN_MONTH);

        if (month == Calendar.FEBRUARY
                && (day == 5 || day == 28)) {
            ret = true;
        } else if (month == Calendar.APRIL
                && (day == 9 || day == 27)) {
            ret = true;
        } else if (month == Calendar.MAY
                && (day == 1 || day == 12
                || (dow == Calendar.SUNDAY && (dowim == 2 || dowim == 3)))) {
            ret = true;
        } else if (month == Calendar.JUNE
                && (day == 4 || day == getMidsummerDay(year))) {
            ret = true;
        } else if (month == Calendar.JULY
                && (day == 6)) {
            ret = true;
        } else if (month == Calendar.OCTOBER
                && (day == 10 || day == 24)) {
            ret = true;
        } else if (month == Calendar.NOVEMBER
                && (day == 6 || (dow == Calendar.SUNDAY && dowim == 2))) {
            ret = true;
        } else if (month == Calendar.DECEMBER
                && (day == 6)) {
            ret = true;
        }
        return ret;
    }
}
