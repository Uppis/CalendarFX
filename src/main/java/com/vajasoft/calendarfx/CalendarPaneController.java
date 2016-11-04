package com.vajasoft.calendarfx;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.Month;
import java.time.format.TextStyle;
import java.time.temporal.ChronoUnit;
import java.time.temporal.WeekFields;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundImage;
import javafx.scene.layout.BackgroundPosition;
import javafx.scene.layout.BackgroundRepeat;
import javafx.scene.layout.BackgroundSize;
import javafx.scene.layout.GridPane;

public class CalendarPaneController implements Initializable {
    private static final Locale LOCALE = Locale.getDefault(Locale.Category.FORMAT);
    private static final int NBR_OF_COLUMNS = 7;
    private static final int NBR_OF_ROWS = 6;
    private static final List<FlagDaySpec> FLAGDAY_CONFIG = readFlagDayConfig();
    private static final Image FLAG = new Image("/img/suomi_top_left.gif");
    private static final Background FLAG_BACKGROUND = new Background(new BackgroundImage(FLAG, BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT, BackgroundPosition.DEFAULT, BackgroundSize.DEFAULT));

    private final LocalDate today = LocalDate.now();
//    private final int firstDayOfWeek = today.getFirstDayOfWeek();
    private LocalDate currentMonth;
    private final boolean flagOn = true;

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
        for (int i = 1; i <= NBR_OF_COLUMNS; i++) {
            Label weekday = (Label)pnlWeekdays.getChildren().get(i - 1);
            weekday.setText(DayOfWeek.of(i).getDisplayName(TextStyle.SHORT, LOCALE));
            if (i == DayOfWeek.SUNDAY.getValue()) {
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
                to.add(lbl, i, j);
            }
        }
    }

    private void maybeShowToday(LocalDate currentMonth) {
        Label lbl = (Label)pnlDays.getChildren().get(gridIndex(today));
        // Reset possibly old "today"
        // If today is in current month, set "today"
        ObservableList<String> styleClass = lbl.getStyleClass();
        if (today.getYear() == currentMonth.getYear() && today.getMonthValue() == currentMonth.getMonthValue()) {
            styleClass.add("today-label");
        } else {
            styleClass.remove("today-label");
        }
    }

    private void moveTo(LocalDate date) {
        currentMonth = LocalDate.of(date.getYear(), date.getMonth(), 1);
        setCalendarToCurrentMonth();
    }

    private void moveToBOY() {
        currentMonth = LocalDate.of(currentMonth.getYear(), Month.JANUARY, 1);
        setCalendarToCurrentMonth();
    }

    private void moveToEOY() {
        currentMonth = LocalDate.of(currentMonth.getYear(), Month.DECEMBER, 1);
        setCalendarToCurrentMonth();
    }

    private void moveDate(ChronoUnit unit, int amount) {
        currentMonth = currentMonth.plus(amount, unit);
        setCalendarToCurrentMonth();
    }

    private void setCalendarToCurrentMonth() {
        fldMonth.setText(currentMonth.getMonth().getDisplayName(TextStyle.FULL_STANDALONE, LOCALE) + " " + currentMonth.getYear());
        maybeShowToday(currentMonth);
        Map<LocalDate, String> flagDays = new LinkedHashMap<>();
        getFlagDays(currentMonth.getYear(), flagDays);
        LocalDate d = currentMonth.plusDays(-gridIndex(currentMonth) - 1);  // start from 1 day "behind" the current month in calendar view
        for (int row = 0; row < NBR_OF_ROWS; row++) {
            boolean daysInRow = false;
            for (int col = 0; col < NBR_OF_COLUMNS; col++) {
                int ix = row * NBR_OF_COLUMNS + col;
                Label lbl = (Label)pnlDays.getChildren().get(ix);
                lbl.setBackground(Background.EMPTY);
                lbl.setTooltip(null);
                lbl.getStyleClass().remove("sunday-label");
                d = d.plusDays(1);
                if (d.getMonth().equals(currentMonth.getMonth())) {
                    lbl.setText(String.valueOf(d.getDayOfMonth()));
                    if (isFeastDay(d)) {
                        lbl.getStyleClass().add("sunday-label");
                    }
                    if (flagOn && flagDays.containsKey(d)) {
                        lbl.setBackground(FLAG_BACKGROUND);
                        lbl.setTooltip(new Tooltip(flagDays.get(d)));
                    }
                    daysInRow = true;
                } else {
                    lbl.setText("");
                }
            }
            Label weekLbl = (Label)pnlWeeks.getChildren().get(row);
            if (daysInRow) {
                int weekNumber = d.get(WeekFields.ISO.weekOfWeekBasedYear());
                weekLbl.setText(String.valueOf(weekNumber));
            } else {
                weekLbl.setText("");
            }
        }
    }

    private int gridIndex(LocalDate day) {
        int start = LocalDate.of(day.getYear(), day.getMonth(), 1).getDayOfWeek().getValue() - 1;  // start == grid index of the 1st of month
        return start + day.getDayOfMonth() - 1;
    }

    @FXML
    private void onCmdPrevYearAction(ActionEvent e) {
        moveDate(ChronoUnit.YEARS, -1);
    }

    @FXML
    private void onCmdFirstMonthAction(ActionEvent e) {
        moveToBOY();
    }

    @FXML
    private void onCmdPrevMonthAction(ActionEvent e) {
        moveDate(ChronoUnit.MONTHS, -1);
    }

    @FXML
    private void onCmdTodayAction(ActionEvent e) {
        moveTo(today);
    }

    @FXML
    private void onCmdNextMonthAction(ActionEvent e) {
        moveDate(ChronoUnit.MONTHS, 1);
    }

    @FXML
    private void onCmdLastMonthAction(ActionEvent e) {
        moveToEOY();
    }

    @FXML
    private void onCmdNextYearAction(ActionEvent e) {
        moveDate(ChronoUnit.YEARS, 1);
    }

    private static boolean isFeastDay(LocalDate date) {
        boolean ret = false;
        DayOfWeek weekday = date.getDayOfWeek();
        int day = date.getDayOfMonth();
        Month month = date.getMonth();

        if (weekday == DayOfWeek.SUNDAY) {
            ret = true;
        } else if (weekday == DayOfWeek.SATURDAY && ((month == Month.OCTOBER && day == 31) || (month == Month.NOVEMBER && day <= 6))) { // pyhäinpäivä
            ret = true;
        } else {
            int year = date.getYear();
            boolean since_1992 = year >= 1992;

            if (month == Month.JANUARY && (day == 1 || (day == 6 && since_1992))) {// // uudenvuoden päivä, loppiainen
                ret = true;
            } else if (month == Month.MAY && day == 1) {// vappu
                ret = true;
            } else if (month == Month.JUNE) { // juhannusaatto ?
                ret = day == getMidsummerDay(year);
            } else if (month == Month.DECEMBER && (day == 6 || (day >= 25 && day <= 26))) {// itsenäisyyspäivä, Joulun pyhät
                ret = true;
            } else { // Pääsiäinen ja helatorstai
                LocalDate eastern = getEastern(year);   // Pääsiäissunnuntai
                if (date.equals(eastern) || date.equals(eastern.minusDays(2)) || date.equals(eastern.plusDays(1))) {
                    ret = true;
                } else if (date.equals(eastern.plusDays(39)) && since_1992) {// helatorstai
                    ret = true;
                }
            }
        }
        return ret;
    }

    private static int getMidsummerDay(int year) {
        // Saturday between 20. - 26.6.
        LocalDate tmp = LocalDate.of(year, Month.JUNE, 20);
        return 26 - tmp.getDayOfWeek().getValue() % DayOfWeek.SUNDAY.getValue();
    }

    private static LocalDate getEastern(int year) {
        Month e_month = Month.MARCH;
        int tmp1 = (19 * (year % 19) + 24) % 30;
        int tmp2 = (2 * (year % 4) + 4 * (year % 7) + 6 * tmp1 + 5) % 7;
        int e_day = 22 + tmp1 + tmp2;

        if (e_day > 31) {
            e_month = Month.APRIL;
            e_day = tmp1 + tmp2 - 9;
            if (e_day == 26) {
                e_day = 19;
            } else if (e_day == 25 && tmp1 == 28 && (year % 19) > 10) {
                e_day = 18;
            }
        }
        return LocalDate.of(year, e_month, e_day);
    }

    private void getFlagDays(int forYear, Map<LocalDate, String> to) {
        FLAGDAY_CONFIG.stream().forEach((fd) -> {
            to.put(fd.getFlagDay(forYear), fd.getDescription());
        });
    }

    private static List<FlagDaySpec> readFlagDayConfig() {
        Pattern CONFIG_LINE = Pattern.compile("(\\d{1,2})\\s+(?:(\\d{1,2}->)|(\\d\\.)|(\\d{1,2}))\\s+([A-Z]+|)\\s+\"(.*)\"");
        try {
            ArrayList<FlagDaySpec> flagDayConfig = new ArrayList<>();
            BufferedReader conf = new BufferedReader(new InputStreamReader(CalendarPaneController.class.getResourceAsStream("/config/flagdays.conf")));
            String line = conf.readLine();
            while (line != null) {
                Matcher m = CONFIG_LINE.matcher(line);
                if (m.matches()) {
                    int month = Integer.parseInt(m.group(1));
                    if (m.group(4) != null) {   // dayOfMonth
                        flagDayConfig.add(new FlagDaySpecFixed(month, Integer.parseInt(m.group(4)), m.group(6)));
                    } else if (m.group(3) != null) {    // orderOfDayOfWeek
                        flagDayConfig.add(new FlagDaySpecOrder(month, Integer.parseInt(m.group(3).substring(0, m.group(3).length() - 1)), DayOfWeek.valueOf(m.group(5)), m.group(6)));
                    } else if (m.group(2) != null) {    // dayRange
                        String range = m.group(2);
                        String startDay = range.substring(0, range.indexOf("->"));
                        flagDayConfig.add(new FlagDaySpecRange(month, Integer.parseInt(startDay), DayOfWeek.valueOf(m.group(5)), m.group(6)));
                    } else {
                        throw new RuntimeException("Invalid configuration: " + line);
                    }
                }
                line = conf.readLine();
            }
            return flagDayConfig;
        } catch (IOException ex) {
            throw new RuntimeException("Failed to read configuration", ex);
        }
    }
}
