package com.calendar.data;

import net.fortuna.ical4j.model.DateTime;

/**
 * Created by NAVER on 2017-07-19.
 */
public class ICalEvent {
    private String start;
    private String end;
    private int month;
    private int date;
    private String summary;

    public String getStart() {
        return start;
    }

    public void setStart(String start) {
        this.start = start;
    }

    public String getEnd() {
        return end;
    }

    public void setEnd(String end) {
        this.end = end;
    }

    public int getMonth() {
        return month;
    }

    public void setMonth(int month) {
        this.month = month;
    }

    public int getDate() {
        return date;
    }

    public void setDate(int date) {
        this.date = date;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }
}
