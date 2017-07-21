package com.calendar;

import java.util.List;

/**
 * Created by NAVER on 2017-07-19.
 */
public class ICalEvent {
    private String start;
    private String end;
    private String summary;
    private Boolean isRecur = false;

    private String frequency;
    private String until = "";
    private List<String> bydayList;
    private int interval;

    private String location;
    private String organizer;
    private List<String> list;

    public ICalEvent(String start, String end, String summary, String location, String organizer, List<String> list) {
        this.start = start;
        this.end = end;
        this.summary = summary;
        this.location = location;
        this.organizer = organizer;
        this.list = list;
    }

    public ICalEvent(){

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
        return until;
    }

    public void setUntil(String until) {
        this.until = until;
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
}
