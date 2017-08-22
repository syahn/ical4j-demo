package com.calendar.util;

import com.calendar.data.ICalEvent;
import net.fortuna.ical4j.model.component.VEvent;

import java.time.Month;
import java.time.Year;
import java.time.YearMonth;

/**
 * Created by NAVER on 2017-08-21.
 */
public class DateUtil {

    public static int getNextMonth(int month) {
        return  Month.of(month).plus(1).getValue();
    }

    public static int getPreMonth(int month) {
        return Month.of(month).minus(1).getValue();
    }

    public static int getYearOfNextMonth(int year, int month) {
        return YearMonth.of(year, month).plusMonths(1).getYear();
    }

    public static int getYearOfPreMonth(int year, int month) {
        return YearMonth.of(year, month).minusMonths(1).getYear();
    }

    public static int getLastDay(int year, int month) {
        int lastDay = daysOfMonth(year, month);
        return YearMonth.of(year, month).atDay(lastDay).getDayOfWeek().getValue();
    }

    public static int daysOfMonth(int year, int month) {
        return YearMonth.of(year, month).lengthOfMonth();
    }

    public static int daysOfYear(int year) {
        return Year.of(year).length();
    }

    public static int getFirstDay(int year, int month) {
        return YearMonth.of(year, month).atDay(1).getDayOfWeek().getValue();
    }

    public static int calculatePeriod(ICalEvent data, VEvent event) {
        int startYear = data.getStartYear();
        int startMonth = data.getStartMonth();
        int startDate = data.getStartDate();
        int endYear = data.getEndYear();
        int endMonth = data.getEndMonth();
        int endDate = data.getEndDate();

        // 종일 이벤트가 아닌 시간 이벤트면 1 더해주기
        int offset = event.getStartDate().getTimeZone() != null ? 1 : 0;
        if (startYear == endYear) {
            if (startMonth == endMonth) {
                return endDate - startDate + offset;
            } else {
                return DateUtil.daysOfMonth(startYear, startMonth) - startDate + endDate + offset;
            }
        } else {
            // TODO: 연 계산하기
        }
        return -1;
    }

    public static int calculateWeekRow(int startIndex) {
        if (startIndex < 7) {
            return 0;
        } else if (startIndex < 14) {
            return 1;
        } else if (startIndex < 21) {
            return 2;
        } else if (startIndex < 28) {
            return 3;
        } else if (startIndex < 35) {
            return 4;
        } else if (startIndex < 42) {
            return 5;
        } else {
            return -1;
        }
    }

    public static int extractYear(String time) {
        return Integer.parseInt(time.substring(0, 4));
    }

    public static int extractMonth(String time) {
        return Integer.parseInt(time.substring(4, 6));
    }

    public static int extractDate(String time) {
        return Integer.parseInt(time.substring(6, 8));
    }
}
