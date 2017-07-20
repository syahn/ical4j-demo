package com.calendar.data;

import java.util.Date;

/**
 * Created by NAVER on 2017-07-14.
 */
public class CalendarData {
    private String event;
    private String date;
    private String iCal;

    public CalendarData(String event, String date, String iCal) {
        this.event = event;
        this.date = date;
        this.iCal = iCal;
    }

    public String getEvent() {
        return event;
    }

    public void setEvent(String event) {
        this.event = event;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getiCal() {
        return iCal;
    }

    public void setiCal(String iCal) {
        this.iCal = iCal;
    }
}
