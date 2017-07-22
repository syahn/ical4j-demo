package com.calendar;

import java.util.List;

/**
 * Created by NAVER on 2017-07-19.
 */
public class ICalEvent {
    private String startDate;
    private int startDay;
    private int startMonth;
    private int startYear;
    private String end;
    private String summary;
    private Boolean isRecur = false;

    private String frequency;
    private String untilDate;
    private int untilDay;
    private int untilMonth;
    private int untilYear;
    private List<String> bydayList;
    private int interval;

    private String location;
    private String organizer;
    private List<String> list;

    public ICalEvent(String start, String end, String summary, String location, String organizer, List<String> list) {
        this.startDate = start;
        this.end = end;
        this.summary = summary;
        this.location = location;
        this.organizer = organizer;
        this.list = list;
    }

    public ICalEvent(){

    }

    public String getStart() {
        return startDate;
    }

    public void setStart(String start) {
        this.startDate = start;
    }

    public String getEnd() {
        return end;
    }

    public void setEnd(String end) {
        this.end = end;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getOrganizer() {
        return organizer;
    }

    public void setOrganizer(String organizer) {
        this.organizer = organizer;
    }

    public List<String> getList() {
        return list;
    }

    public void setList(List<String> list) {
        this.list = list;
    }

    public String getFrequency() {
        return frequency;
    }

    public void setFrequency(String frequency) {
        this.frequency = frequency;
    }

    public String getUntil() {
        return untilDate;
    }

    public void setUntil(String until) {
        this.untilDate = until;
    }

    public List<String> getBydayList() {
        return bydayList;
    }

    public void setBydayList(List<String> bydayList) {
        this.bydayList = bydayList;
    }

    public int getInterval() {
        return interval;
    }

    public void setInterval(int interval) {
        this.interval = interval;
    }

    public Boolean getIsRecur() {
        return isRecur;
    }

    public void setIsRecur(Boolean recur) {
        isRecur = recur;
    }

    public int getStartDay() {
        return startDay;
    }

    public void setStartDay(int startDay) {
        this.startDay = startDay;
    }

    public int getStartMonth() {
        return startMonth;
    }

    public void setStartMonth(int startMonth) {
        this.startMonth = startMonth;
    }

    public int getStartYear() {
        return startYear;
    }

    public void setStartYear(int startYear) {
        this.startYear = startYear;
    }

    public Boolean getRecur() {
        return isRecur;
    }

    public void setRecur(Boolean recur) {
        isRecur = recur;
    }

    public int getUntilDay() {
        return untilDay;
    }

    public void setUntilDay(int untilDay) {
        this.untilDay = untilDay;
    }

    public int getUntilMonth() {
        return untilMonth;
    }

    public void setUntilMonth(int untilMonth) {
        this.untilMonth = untilMonth;
    }

    public int getUntilYear() {
        return untilYear;
    }

    public void setUntilYear(int untilYear) {
        this.untilYear = untilYear;
    }
}
