package com.calendar.data;

import java.util.List;

/**
 * Created by NAVER on 2017-07-19.
 */
public class ICalFilteredEvent {
    private int index;
    private String summary;
    private String type;
    private String uid;

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
}
