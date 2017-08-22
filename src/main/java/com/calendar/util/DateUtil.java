package com.calendar.util;

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
