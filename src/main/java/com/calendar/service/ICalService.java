package com.calendar.service;

import com.calendar.data.*;
import com.calendar.util.DateUtil;
import com.calendar.util.FilterUtil;
import net.fortuna.ical4j.data.CalendarBuilder;
import net.fortuna.ical4j.data.ParserException;
import net.fortuna.ical4j.filter.Filter;
import net.fortuna.ical4j.filter.PeriodRule;
import net.fortuna.ical4j.model.Calendar;
import net.fortuna.ical4j.model.Period;
import net.fortuna.ical4j.model.component.VEvent;
import net.fortuna.ical4j.model.component.VToDo;
import org.springframework.stereotype.Service;

import java.io.FileInputStream;
import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by NAVER on 2017-07-20.
 */
@Service
public class ICalService {

    public Calendar parseFile(String file) throws IOException, ParserException {
        FileInputStream fin = new FileInputStream(file);
        CalendarBuilder builder = new CalendarBuilder();

        return builder.build(fin);
    }

    //일정리스트 만들기
    public ICalFilteredData filterData(Calendar calendar, int month, int year) throws ParseException {
        // Setting 값 초기화
        Setting setting = setUp(year, month);

        // 이벤트와 todo리스트 추출
        List<VEvent> events = calendar.getComponents("VEVENT");
        List<VToDo> todos = calendar.getComponents("VTODO");

        // 필터링
        events = filterByValidPeriod(events, todos, setting);
        List<ICalEvent> resolvedEventList = resolveDataToICalEvent(events, setting);
        ICalFilteredData filteredData = filterByIndex(resolvedEventList, todos, setting);

        return filteredData;
    }

    private Setting setUp(int year, int month) {
        Setting setting = new Setting();
        setting.setCurrentMonth(month);
        setting.setCurrentYear(year);

        return setting;
    }

    private List<VEvent> filterByValidPeriod(
            List<VEvent> events,
            List<VToDo> todos,
            Setting setting
    ) throws ParseException {
        // 전달 23일 부터 다음 달 6일까지의 기간 설정
        Period period = FilterUtil.makeValidPeriod(setting);
        Filter filter = new Filter(new PeriodRule(period));

        return (List<VEvent>) filter.filter(events);
    }


    private List<ICalEvent> resolveDataToICalEvent(List<VEvent> events, Setting setting) {
        List<ICalEvent> eventList = new ArrayList<>();

        for (VEvent event : events) {
            ICalEvent data = FilterUtil.VEventToICalEvent(event, setting);
            eventList.add(data);
        }

        return eventList;
    }

    private ICalFilteredData filterByIndex(
            List<ICalEvent> eventList,
            List<VToDo> todoList,
            Setting setting
    ) {
        ICalFilteredData filteredData = new ICalFilteredData();

        List<ICalTodo> filteredTodoList = filterTodoListByIndex(todoList, setting);
        List<ICalFilteredEvent> filteredEventList = filterEventListByIndex(eventList, setting);

        filteredData.setTodoList(filteredTodoList);
        filteredData.setEventList(filteredEventList);

        return filteredData;
    }

    private List<ICalTodo> filterTodoListByIndex(List<VToDo> todoList, Setting setting) {
        List<ICalTodo> filteredTodoList = new ArrayList<>();

        for (VToDo todo : todoList) {
            ICalTodo data = FilterUtil.VTodoToICalTodo(todo, setting);
            filteredTodoList.add(data);
        }

        return filteredTodoList;
    }

    private List<ICalFilteredEvent> filterEventListByIndex(
            List<ICalEvent> eventList,
            Setting setting
    ) {
        List<ICalFilteredEvent> filteredEventList = new ArrayList<>();

        for (ICalEvent event : eventList) {
            // 반복 없는 일정
            if (event.getRecur() == false) {
                filteredEventList = FilterUtil.FilterNoneRecurEvent(event, filteredEventList);
            }
            // 반복 일정
            else {
                FilterUtil.filterRecurEvent(event, filteredEventList, setting);
            }
        }
        // 우선 순위에 따라 정렬
        Collections.sort(filteredEventList, new ICalComparator());

        return filteredEventList;
    }
}