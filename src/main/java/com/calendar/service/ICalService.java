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
        System.out.println("1");
        events = filterByValidPeriod(events, todos, setting);
        System.out.println("2");
        List<ICalEvent> resolvedEventList = resolveDataToICalEvent(events, setting);
        System.out.println("3");
        ICalFilteredData filteredData = filterByIndex(resolvedEventList, todos, setting);
        System.out.println("4" + filteredData.toString());
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
        int currentYear = setting.getCurrentYear();
        int currentMonth = setting.getCurrentMonth();

        // 전달 23일 부터 다음 달 6일까지의 기간 설정
        Period period = FilterUtil.makeValidPeriod(currentYear, currentMonth);
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

        //System.out.println("filtered todo" + filteredTodoList.toString());
        return filteredTodoList;
    }

    private List<ICalFilteredEvent> filterEventListByIndex(List<ICalEvent> eventList, Setting setting) {

        List<ICalFilteredEvent> filteredEventList = new ArrayList<>();

        for (ICalEvent event : eventList) {

            // 반복 없는 일정
            if (event.getRecur() == false) {
                System.out.println("list1" + filteredEventList.toString());
                filteredEventList = FilterUtil.FilterNoneRecurEvent(event, filteredEventList);
                System.out.println("list2" + filteredEventList.toString());
            }
            // 반복 일정
            else {

                FilterUtil.FilterRecurEvent(event, filteredEventList, setting);

            }
        }

        Collections.sort(filteredEventList, new ICalComparator());

        System.out.println(filteredEventList);

        return filteredEventList;
    }

    private void addEventToFilteredEvents(
            String type,
            ICalEvent event,
            List<ICalFilteredEvent> filteredEventList
    ) {
        int startIndex = event.getStartIndex();
        int endIndex = event.getEndIndex();
        int period = event.getPeriod();
        int startHour = event.getStartHour();
        int startMinute = event.getStartMinute();

        if (startIndex >= 0 && startIndex < 42 || endIndex >= 0 && endIndex < 42) {
            ICalFilteredEvent data = new ICalFilteredEvent();
            data.setSummary(event.getSummary());
            data.setIndex(startIndex);
            data.setPeriod(period);
            data.setUid(event.getUid());
            data.setType(type);
            data.setEndIndex(endIndex);
            data.setWeekRow(DateUtil.calculateWeekRow(startIndex));
            data.setStartHour(startHour);
            data.setStartMinute(startMinute);
            data.setIsAnniversary(event.getIsAnniversary());
            data.setTimeLabel(event.getTimeLabel());

            filteredEventList.add(data);
        }
    }

    //마지막째 주 요일 반복
    private void addLastWeekRecurEventToFilteredEvents(
            String type,
            ICalEvent event,
            List<ICalFilteredEvent> filteredEventList,
            Setting setting
    ) {
        int calculatedIdx = FilterUtil.calIndexOfLastWeekRecurEvent(event, setting);
        event.setStartIndex(calculatedIdx);

        addEventToFilteredEvents(type, event, filteredEventList);
    }


    //몇째 주 요일 반복
    private void addDayRecurEventToFilteredEvents(
            ICalEvent event,
            List<ICalFilteredEvent> filteredEventList,
            String type,
            Setting setting
    ) {
        int currentYear = setting.getCurrentYear();
        int currentMonth = setting.getCurrentMonth();
        int startDayNum = event.getStartDayNum();
        int startMonth = event.getStartMonth();
        int setPos = event.getBySetPos();
        int preYear = DateUtil.getYearOfPreMonth(currentYear, currentMonth);
        int nextYear = DateUtil.getYearOfNextMonth(currentYear, currentMonth);
        int preMonth = DateUtil.getPreMonth(currentMonth);
        int nextMonth = DateUtil.getNextMonth(currentMonth);
        int daysOfCurrentMonth = DateUtil.daysOfMonth(currentYear, currentMonth);

        if (type.equals("MONTHLY")) {
            int firstIndex = DateUtil.getFirstDay(currentYear, currentMonth);
            int targetIndex = FilterUtil.calIndexOfDayRecurEvent(event, currentYear, currentMonth);

            //이벤트 시작이 해당 인덱스보다 크면 현재 달 이후에 이벤트 시작이라는 것
            if (event.getStartIndex() <= targetIndex) {
                event.setStartIndex(targetIndex);
                addEventToFilteredEvents(type, event, filteredEventList);
            }

            //이전달의 이벤트는 표시
            if (startMonth <= preMonth || preYear < currentYear) {
                //4,5째주면 표시가능
                if (setPos == 4 || setPos == 5) {
                    int firstIndexForPre = DateUtil.getFirstDay(preYear, preMonth);
                    int targetIndexForPre = FilterUtil.calIndexOfDayRecurEvent(event, preYear, preMonth);
                    int lastIndexForPre = firstIndexForPre + DateUtil.daysOfMonth(preYear, preMonth);
                    if (targetIndexForPre >= lastIndexForPre - DateUtil.getFirstDay(currentYear, currentMonth)
                            && targetIndexForPre < firstIndexForPre + DateUtil.daysOfMonth(preYear, startMonth)) {

                        event.setStartIndex(startDayNum == 8 ? 0 : startDayNum - 1);

                        addEventToFilteredEvents(type, event, filteredEventList);
                    }
                }
            }
            //다음달 이벤트중 현재월뷰에 표시될 것
            if (setPos == 1) {

                int targetIndexForNext = targetIndex + 7 * 4;
                targetIndexForNext = (targetIndexForNext <= (firstIndex + daysOfCurrentMonth - 1))
                        ? targetIndex + 7 * 5
                        : targetIndexForNext;

                if (firstIndex + daysOfCurrentMonth - 1 < targetIndexForNext) {
                    event.setStartIndex(targetIndexForNext);
                    addEventToFilteredEvents(type, event, filteredEventList);
                }
            }
            return;
        }

        //YEARLY
        if (startMonth == currentMonth) {
            int targetIndex = FilterUtil.calIndexOfDayRecurEvent(event, currentYear, currentMonth);

            event.setStartIndex(targetIndex);
            addEventToFilteredEvents(type, event, filteredEventList);

        } else if (startMonth == preMonth) {
            //4,5째주면 표시가능
            if (setPos == 4 || setPos == 5) {
                int firstIndex = DateUtil.getFirstDay(preYear, startMonth);
                int targetIndex = FilterUtil.calIndexOfDayRecurEvent(event, preYear, startMonth);
                int lastIndexForPre = firstIndex + DateUtil.daysOfMonth(preYear, startMonth);
                if (targetIndex >= lastIndexForPre - DateUtil.getFirstDay(currentYear, currentMonth) && targetIndex < firstIndex + DateUtil.daysOfMonth(preYear, startMonth)) {
                    event.setStartIndex(startDayNum == 8 ? 0 : startDayNum - 1);
                    addEventToFilteredEvents(type, event, filteredEventList);
                }
            }
        } else if (startMonth == nextMonth) {

            if (setPos == 1) {
                int firstIndex = DateUtil.getFirstDay(nextYear, startMonth);
                int currentFirstIndex = DateUtil.getFirstDay(currentYear, currentMonth);
                int currentLastDay = DateUtil.getLastDay(currentYear, currentMonth);
                int daysToAdd = DateUtil.daysOfMonth(currentYear, currentMonth) - 1;
                int currentLastIndex = currentFirstIndex + daysToAdd;

                if (startDayNum > firstIndex) { // 테이블의 0번째 row에 해당 요일이 포함되는 경우
                    if ((startDayNum - 1) != 0) {//일요일 아니면 무조건 표시됨
                        event.setStartIndex(currentLastIndex + startDayNum - 1 - currentLastDay);
                        addEventToFilteredEvents(type, event, filteredEventList);
                    }
                } // 그 이후는 고려할 필요 없음
            }
        }
    }


}