package com.calendar.data;

import net.fortuna.ical4j.model.Date;
import net.fortuna.ical4j.model.DateTime;

/**
 * Created by NAVER on 2017-07-19.
 */
public class ICalEvent {
    private String start;
    private String end;
    private int month;
    private int startDate;
    private int endDate;
    private String summary;

    //Recurrent field
    private Boolean isRecur;
    private String frequency;
    private String until;
    private int untilYear;
    private int untilMonth;
    private int untilDate;
    private int interval;

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

    public int getStartDate() {
        return startDate;
    }

    public void setStartDate(int startDate) {
        this.startDate = startDate;
    }

    public int getEndDate() {
        return endDate;
    }

    public void setEndDate(int endDate) {
        this.endDate = endDate;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public Boolean getRecur() {
        return isRecur;
    }

    public void setRecur(Boolean recur) {
        isRecur = recur;
    }

    public int getInterval() {
        return interval;
    }

    public void setInterval(int interval) {
        this.interval = interval;
    }

    public String getFrequency() {
        return frequency;
    }

    public void setFrequency(String frequency) {
        this.frequency = frequency;
    }


    public int getUntilYear() {
        return untilYear;
    }

    public void setUntilYear(int untilYear) {
        this.untilYear = untilYear;
    }

    public int getUntilMonth() {
        return untilMonth;
    }

    public void setUntilMonth(int untilMonth) {
        this.untilMonth = untilMonth;
    }

    public int getUntilDate() {
        return untilDate;
    }

    public void setUntilDate(int untilDate) {
        this.untilDate = untilDate;
    }

    public String getUntil() {
        return until;
    }

    public void setUntil(String until) {
        this.until = until;
    }
}
