package com.calendar;

import net.fortuna.ical4j.model.DateTime;

import java.util.Date;
import java.util.List;

/**
 * Created by NAVER on 2017-07-18.
 */
public class DefaultScheduleModel {
    private String start;
    private String end;
    private String summary;
    private String location;
    private String organizer;
    private List<String> list;

    public DefaultScheduleModel(String start, String end, String summary, String location, String organizer, List<String> list) {
        this.start = start;
        this.end = end;
        this.summary = summary;
        this.location = location;
        this.organizer = organizer;
        this.list = list;
    }
//
//    public DefaultScheduleModel(String start, String end, String summary, String location) {
//        this.start = start;
//        this.end = end;
//        this.summary = summary;
//        this.location = location;
//    }

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
}
