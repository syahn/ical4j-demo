package com.calendar.util;

import com.calendar.data.ICalEvent;
import com.calendar.data.ICalFilteredEvent;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by NAVER on 2017-08-22.
 */
public class FilterUtilTest {
    @Test
    public void makeValidPeriod() throws Exception {
    }

    @Test
    public void calIndexOfLastWeekRecurEvent() throws Exception {
    }

    @Test
    public void calIndexOfDayRecurEvent() throws Exception {
    }

    @Test
    public void VTodoToICalTodo() throws Exception {
    }

    @Test
    public void VEventToICalEvent() throws Exception {
    }

    @Test
    public void calculateIndexOfDate() throws Exception {
    }

    @Test
    public void calculatePeriod() throws Exception {
    }

    @Test
    public void addEventToFilteredEvents() throws Exception {
    }

    @Test
    public void makeFilteredEvent() throws Exception {
    }

    @Test
    public void isVaildEvent() throws Exception {
    }

    @Test
    public void filterRecurEvent() throws Exception {
    }

    @Test
    public void filterNoneRecurEvent() throws Exception {
    }

    @Test
    public void filterDaily() throws Exception {

        //daily 일정만 있는 ics 파싱해서 ICalevent로 만들기
        ICalEvent event = new ICalEvent("uid","dateString");

        //list
        List<ICalFilteredEvent> filteredEventList = new ArrayList<>();

        //
        FilterUtil.FilterDaily(event,filteredEventList);

        //확인
        assert(filteredEventList, expected);


    }

    @Test
    public void filterWeekly() throws Exception {
    }

    @Test
    public void filterMonthly() throws Exception {
    }

    @Test
    public void filterYearly() throws Exception {
    }

    @Test
    public void addLastWeekRecurEventToFilteredEvents() throws Exception {
    }

    @Test
    public void addDayRecurEventToFilteredEvents() throws Exception {
    }

}