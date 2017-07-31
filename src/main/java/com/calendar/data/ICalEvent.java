package com.calendar.data;

import net.fortuna.ical4j.model.WeekDayList;

import java.util.List;

/**
 * Created by NAVER on 2017-07-19.
 */
public class ICalEvent {

    private String uid;
    private String start;
    private String end;
    private String summary;
    private Boolean isRecur = false;
    private String startDay;
    private int startDate;
    private int startMonth;
    private int startYear;
    private int endDate;
    private int endMonth;
    private int endYear;
    private int period;
    private String until;
    private String frequency;
    private String untilDay;
    private int untilDate;
    private int untilMonth;
    private int untilYear;
    private WeekDayList byDayList;
    private List<Integer> startDayList; // for weekly
    private int startDayNum;
    private int interval;
    private String location;
    private String organizer;
    private List<String> list;
    private int startIndex;
    private int endIndex;
    private int byMonthDay;
    private int bySetPos;
    private int weekRow;

    public int getWeekRow() {
        return weekRow;
    }

    public void setWeekRow(int weekRow) {
        this.weekRow = weekRow;
    }

    public int getPeriod() {
        return period;
    }

    public void setPeriod(int period) {
        this.period = period;
    }

    public int getEndDate() {
        return endDate;
    }

    public void setEndDate(int endDate) {
        this.endDate = endDate;
    }

    public int getEndMonth() {
        return endMonth;
    }

    public void setEndMonth(int endMonth) {
        this.endMonth = endMonth;
    }

    public int getEndYear() {
        return endYear;
    }

    public void setEndYear(int endYear) {
        this.endYear = endYear;
    }

    public WeekDayList getByDayList() {
        return byDayList;
    }

    public void setByDayList(WeekDayList byDayList) {
        this.byDayList = byDayList;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

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

    public String getStartDay() {
        return startDay;
    }

    public void setStartDay(String startDay) {
        this.startDay = startDay;
    }

    public int getStartDate() {
        return startDate;
    }

    public void setStartDate(int startDate) {
        this.startDate = startDate;
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

    public String getUntil() {
        return until;
    }

    public void setUntil(String until) {
        this.until = until;
    }

    public String getFrequency() {
        return frequency;
    }

    public void setFrequency(String frequency) {
        this.frequency = frequency;
    }

    public String getUntilDay() {
        return untilDay;
    }

    public void setUntilDay(String untilDay) {
        this.untilDay = untilDay;
    }

    public int getUntilDate() {
        return untilDate;
    }

    public void setUntilDate(int untilDate) {
        this.untilDate = untilDate;
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

    public List<Integer> getStartDayList() {
        return startDayList;
    }

    public void setStartDayList(List<Integer> startDayList) {
        this.startDayList = startDayList;
    }

    public int getStartDayNum() {
        return startDayNum;
    }

    public void setStartDayNum(int startDayNum) {
        this.startDayNum = startDayNum;
    }

    public int getInterval() {
        return interval;
    }

    public void setInterval(int interval) {
        this.interval = interval;
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

    public int getStartIndex() {
        return startIndex;
    }

    public void setStartIndex(int startIndex) {
        this.startIndex = startIndex;
    }

    public int getEndIndex() {
        return endIndex;
    }

    public void setEndIndex(int endIndex) {
        this.endIndex = endIndex;
    }

    public int getByMonthDay() {
        return byMonthDay;
    }

    public void setByMonthDay(int byMonthDay) {
        this.byMonthDay = byMonthDay;
    }

    public int getBySetPos() {
        return bySetPos;
    }

    public void setBySetPos(int bySetPos) {
        this.bySetPos = bySetPos;
    }
}
