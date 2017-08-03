package com.calendar.data;

/**
 * Created by NAVER on 2017-07-19.
 */
public class ICalFilteredEvent {
    private int index;
    private String summary;
    private String type;
    private String uid;
    private int period;
    private int weekRow;
    private int endIndex;
    private int startHour;
    private int startMinute;
    private int isAnniversary;
    private String timeLabel=null;


    public int getPeriod() {
        return period;
    }

    public void setPeriod(int period) {
        this.period = period;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public int getWeekRow() {
        return weekRow;
    }

    public void setWeekRow(int weekRow) {
        this.weekRow = weekRow;
    }

    public int getEndIndex() {
        return endIndex;
    }

    public void setEndIndex(int endIndex) {
        this.endIndex = endIndex;
    }

    public int getStartHour() {
        return startHour;
    }

    public void setStartHour(int startHour) {
        this.startHour = startHour;
    }

    public int getStartMinute() {
        return startMinute;
    }

    public void setStartMinute(int startMinute) {
        this.startMinute = startMinute;
    }

    public int getIsAnniversary() {
        return isAnniversary;
    }

    public void setIsAnniversary(int isAnniversary) {
        this.isAnniversary = isAnniversary;
    }

    public String getTimeLabel() {
        return timeLabel;
    }

    public void setTimeLabel(String timeLabel) {
        this.timeLabel = timeLabel;
    }
}
