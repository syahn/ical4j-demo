package com.calendar.service;

import com.calendar.data.*;
import com.calendar.util.DateUtil;
import com.calendar.util.FilterUtil;
import net.fortuna.ical4j.data.CalendarBuilder;
import net.fortuna.ical4j.data.ParserException;
import net.fortuna.ical4j.filter.Filter;
import net.fortuna.ical4j.filter.PeriodRule;
import net.fortuna.ical4j.model.Calendar;
import net.fortuna.ical4j.model.DateTime;
import net.fortuna.ical4j.model.Period;
import net.fortuna.ical4j.model.component.VEvent;
import net.fortuna.ical4j.model.component.VToDo;
import org.springframework.stereotype.Service;

import java.io.FileInputStream;
import java.io.IOException;
import java.text.ParseException;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
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
        Setting setting = new Setting();
        setting.setCurrentMonth(month);
        setting.setCurrentYear(year);

        List<VEvent> events = calendar.getComponents("VEVENT");
        List<VToDo> todos = calendar.getComponents("VTODO");

        ICalFilteredData list = filterByValidPeriod(events, todos, setting);

        return list;
    }

    private ICalFilteredData filterByValidPeriod(
            List<VEvent> events,
            List<VToDo> todos,
            Setting setting
    ) throws ParseException {
        int currentYear = setting.getCurrentYear();
        int currentMonth = setting.getCurrentMonth();

        // 전달 23일 부터 다음 달 6일까지의 기간 설정
        Period period = makeValidPeriod(currentYear, currentMonth);
        Filter filter = new Filter(new PeriodRule(period));

        events = (List<VEvent>) filter.filter(events);
        List<ICalEvent> resolvedEventList = resolveDataToICalEvent(events, setting);

        return filterByIndex(resolvedEventList, todos, setting);
    }

    private Period makeValidPeriod(int year, int month) throws ParseException {
        YearMonth currentYearMonth = YearMonth.of(year, month);
        int preYear = currentYearMonth.minusMonths(1).getYear();
        int nextYear = currentYearMonth.plusMonths(1).getYear();
        int preMonth = DateUtil.getPreMonth(month);
        int nextMonth = DateUtil.getNextMonth(month);

        LocalDate lastWeekOfPrevMonth = YearMonth.of(preYear, preMonth).atDay(23);
        LocalDate firstWeekOfNextMonth = YearMonth.of(nextYear, nextMonth).atDay(6);
        DateTime startDate = new DateTime(lastWeekOfPrevMonth
                .format(DateTimeFormatter.ofPattern("yyyyMMdd")) + "T000000Z");//전달 23일
        DateTime endDate = new DateTime(firstWeekOfNextMonth
                .format(DateTimeFormatter.ofPattern("yyyyMMdd")) + "T000000Z");//다음달 6일

        //기간 만들기 - RRule 밑에 EXPIR있는 경우 인식 못함
        return new Period(startDate, endDate);
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
        int currentYear = setting.getCurrentYear();
        int currentMonth = setting.getCurrentMonth();
        List<ICalTodo> filteredTodoList = new ArrayList<>();

        for (VToDo todo : todoList) {

            ICalTodo data = FilterUtil.VTodoToICalTodo(todo,setting);
            filteredTodoList.add(data);

        }

        System.out.println(filteredTodoList);
        return filteredTodoList;
    }

    private List<ICalFilteredEvent> filterEventListByIndex(List<ICalEvent> eventList, Setting setting) {

        List<ICalFilteredEvent> filteredEventList = new ArrayList<>();

        for (ICalEvent event : eventList) {

            // 반복 없는 일정
            if (event.getRecur() == false) {

                FilterUtil.FilterNoneRecurEvent(event, filteredEventList);

            }
            // 반복 일정
            else {

                FilterUtil.FilterRecurEvent(event, filteredEventList, setting);

            }
        }

        Collections.sort(filteredEventList,new ICalComparator());

        System.out.println(filteredEventList);

        return filteredEventList;
    }
}