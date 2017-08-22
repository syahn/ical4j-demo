package com.calendar.util;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Created by NAVER on 2017-08-21.
 */
public class DateUtilTest {
    @Test
    public void calculateWeekRow() throws Exception {
        assertEquals(0, DateUtil.calculateWeekRow(6));
        assertEquals(1, DateUtil.calculateWeekRow(12));
        assertEquals(2, DateUtil.calculateWeekRow(20));
        assertEquals(3, DateUtil.calculateWeekRow(27));
        assertEquals(-1, DateUtil.calculateWeekRow(41));
    }

    @Test
    public void getNextMonth() throws Exception {
        assertEquals(1, DateUtil.getNextMonth(12));
    }

    @Test
    public void getPreMonth() throws Exception {
        assertEquals(12, DateUtil.getPreMonth(1));
    }

    @Test
    public void getYearOfNextMonth() throws Exception {
        assertEquals(2018, DateUtil.getYearOfNextMonth(2017, 12));
    }

    @Test
    public void getYearOfPreMonth() throws Exception {
        assertEquals(2017, DateUtil.getYearOfPreMonth(2018,1));
    }

    @Test
    public void getLastDay() throws Exception {
        assertEquals(4, DateUtil.getLastDay(2017, 8));
    }

    @Test
    public void daysOfMonth() throws Exception {
        assertEquals(31, DateUtil.daysOfMonth(2017, 8));
    }

    @Test
    public void daysOfYear() throws Exception {
        assertEquals(365, DateUtil.daysOfYear(2017));
    }

    @Test
    public void getFirstDay() throws Exception {
        assertEquals(2, DateUtil.getFirstDay(2017, 8));
    }
}