package com.calendar.service;

import com.calendar.data.*;
import com.calendar.util.DateUtil;
import com.calendar.util.FilterUtil;
import net.fortuna.ical4j.data.CalendarBuilder;
import net.fortuna.ical4j.data.ParserException;
import net.fortuna.ical4j.filter.Filter;
import net.fortuna.ical4j.filter.PeriodRule;
import net.fortuna.ical4j.model.*;
import net.fortuna.ical4j.model.Period;
import net.fortuna.ical4j.model.component.VEvent;
import net.fortuna.ical4j.model.component.VToDo;
import net.fortuna.ical4j.model.property.RRule;
import org.springframework.stereotype.Service;

import java.io.FileInputStream;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjusters;
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
            ICalEvent data = new ICalEvent();
            data.setUid(event.getUid().getValue());
            data.setSummary(event.getSummary().getValue());
            data.setStart(event.getStartDate().getValue());
            data.setStartDate(DateUtil.extractDate(data.getStart()));
            data.setStartMonth(DateUtil.extractMonth(data.getStart()));
            data.setStartYear(DateUtil.extractYear(data.getStart()));
            data.setEnd(event.getEndDate().getValue());
            data.setEndDate(DateUtil.extractDate(data.getEnd()));
            data.setEndMonth(DateUtil.extractMonth(data.getEnd()));
            data.setEndYear(DateUtil.extractYear(data.getEnd()));
            data.setStartIndex(FilterUtil.calIndexOfDate(data, "start", setting));//모든 이벤트 필수
            data.setEndIndex(FilterUtil.calIndexOfDate(data, "end", setting));//기간 일정만
            data.setWeekRow(DateUtil.calculateWeekRow(data.getStartIndex()));
            data.setPeriod(DateUtil.calculatePeriod(data, event));

            //기념일 컴포넌트
            if (event.getProperty("X-NAVER-ANNIVERSARY") != null) {
                data.setIsAnniversary(Integer.parseInt(event.getProperty("X-NAVER-ANNIVERSARY").getValue()));
            }

            //시간 데이터 포함
            if (event.getStartDate().getTimeZone() != null) {
                SimpleDateFormat hdf = new SimpleDateFormat("HH");
                SimpleDateFormat mdf = new SimpleDateFormat("mm");
                data.setStartHour(Integer.parseInt(hdf.format(event.getStartDate().getDate())));
                data.setStartMinute(Integer.parseInt(mdf.format(event.getStartDate().getDate())));
                data.setEndHour(Integer.parseInt(hdf.format(event.getEndDate().getDate())));
                data.setEndMinute(Integer.parseInt(mdf.format(event.getEndDate().getDate())));

                String timeLabel;
                int startHour = data.getStartHour();

                if (startHour > 11) {
                    int hour = startHour == 12 ? 12 : startHour - 12;
                    timeLabel = "(오후 " + hour + ":" + data.getStartMinute() + ") ";
                } else {
                    int hour = startHour == 0 ? 12 : startHour;
                    timeLabel = "(오전 " + hour + ":" + data.getStartMinute() + ") ";
                }
                data.setTimeLabel(timeLabel);
                data.setSummary(timeLabel + data.getSummary());//이벤트 내용 앞에 기본적으로 붙이기
            }

            //반복있는 이벤트의 경우 추가 정보 삽입
            if (event.getProperty("RRULE") != null) {
                RRule rule = (RRule) event.getProperty("RRULE");
                data.setRecur(true);
                data.setInterval(rule.getRecur().getInterval());
                data.setFrequency(rule.getRecur().getFrequency());
                data.setByDayList(rule.getRecur().getDayList());
                if (rule.getRecur().getUntil() != null) {
                    data.setUntil(rule.getRecur().getUntil().toString());
                    data.setUntilDate(DateUtil.extractDate(data.getUntil()));
                    data.setUntilMonth(DateUtil.extractMonth(data.getUntil()));
                    data.setUntilYear(DateUtil.extractYear(data.getUntil()));
                    data.setEndIndex(FilterUtil.calIndexOfDate(data, "untilEnd",setting)); //until 존재시 endIndex를 until date에 맞춰줌
                }

                /* 요일 반복 위한 이벤트 시작 날짜들 리스트(일,금 이면 1,6) */
                if (rule.getRecur().getDayList() != null) {//요일 반복일때만 daylist 있음

                    ArrayList<Integer> tempDayList = new ArrayList<>();
                    for (WeekDay day : rule.getRecur().getDayList()) {
                        tempDayList.add(WeekDay.getCalendarDay(day));
                    }
                    data.setStartDayList(tempDayList);

                    //시작 날짜의 요일 dayofWeek 포함( 나중에 시작일 구분 시 필요) - 시작일이 요일이면
                    LocalDate date = LocalDate.of(data.getStartYear(), data.getStartMonth(), data.getStartDate());
                    DayOfWeek dayOfWeek = date.getDayOfWeek();
                    int startDayNum = dayOfWeek.getValue();
                    data.setStartDayNum(startDayNum + 1);//하나 더해야 ical4j의 weekDay와 매칭됨
                }

                //연반복중 마지막 날이 조건인 경우 - BYMONTHDAY = -1
                if (rule.getRecur().getMonthDayList().size() > 0) {
                    data.setByMonthDay(rule.getRecur().getMonthDayList().get(0));
                }

                if (rule.getRecur().getSetPosList().size() > 0) {
                    data.setBySetPos(rule.getRecur().getSetPosList().get(0));
                }
            }
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
            String uid = todo.getUid().getValue();
            String due = todo.getDue().getValue();
            String summary = todo.getSummary().getValue();

            int dueYear = DateUtil.extractYear(due);
            int dueMonth = DateUtil.extractMonth(due);
            int dueDate = DateUtil.extractDate(due);
            int index = DateUtil.getFirstDay(currentYear, currentMonth) + dueDate - 1;
            int weekRow = DateUtil.calculateWeekRow(index);

            ICalTodo todoData = new ICalTodo();
            todoData.setUid(uid);
            todoData.setSummary(summary);

            if (dueYear == currentYear && dueMonth == currentMonth) {
                todoData.setDueYear(dueYear);
                todoData.setDueMonth(dueMonth);
                todoData.setDueDate(dueDate);
                todoData.setIndex(index);
                todoData.setWeekRow(weekRow);
                todoData.setType("TODO");
                filteredTodoList.add(todoData);
            }
        }
        return filteredTodoList;
    }

    private List<ICalFilteredEvent> filterEventListByIndex(List<ICalEvent> eventList, Setting setting) {
        int currentYear = setting.getCurrentYear();
        int currentMonth = setting.getCurrentMonth();
        List<ICalFilteredEvent> filteredEventList = new ArrayList<>();

        for (ICalEvent event : eventList) {
            List<Integer> startDayList = event.getStartDayList();
            WeekDayList byDayList = event.getByDayList();
            String frequency = event.getFrequency();
            int startIndex = event.getStartIndex();
            int endIndex = event.getEndIndex();
            int startDayNum = event.getStartDayNum();
            int interval = event.getInterval();
            int startYear = event.getStartYear();
            int startMonth = event.getStartMonth();
            int untilDate = event.getUntilDate();
            int setPos = event.getBySetPos();
            boolean recur = event.getRecur();
            int byMonthDay = event.getByMonthDay();
            int period = event.getPeriod();
            int weekRow = event.getWeekRow();
            int end = untilDate == 0 ? 42 : endIndex + 1;
            int daysOfMonth = DateUtil.daysOfMonth(currentYear, currentMonth-1);

            // 반복 없는 일정
            if (recur == false) {
                // 당일 일정
                if (period == 1) {
                    addEventToFilteredEvents("DAY", event, filteredEventList);
                }
                // 여러날 일정
                else if (period > 1) {
                    int tempPeriod = period;
                    int currentWeekRow = weekRow;

                    while (tempPeriod != 0) {
                        if (startIndex < 0) {
                            tempPeriod += startIndex;
                            //전달의 23일부터 불러오는 기간 일정 데이터가 만약 현재달에 걸치지않으면 예외임
                            if (tempPeriod <= 0) break;
                            startIndex = 0;
                        }

                        for (int idx = currentWeekRow * 7; idx < currentWeekRow * 7 + 7; idx++) {
                            int nextIdxOfWeek = currentWeekRow * 7 + 7;

                            if (idx == startIndex) {
                                //만약 일정이 한 주에 이미 모두 꽉 채워지면 다음주로 넘겨야함
                                if (startIndex + tempPeriod - 1 >= nextIdxOfWeek) {
                                    event.setStartIndex(startIndex);
                                    event.setPeriod(nextIdxOfWeek - startIndex);
                                    event.setWeekRow(currentWeekRow);
                                    addEventToFilteredEvents("PERIOD", event, filteredEventList);

                                    tempPeriod -= (nextIdxOfWeek - startIndex); // 뿌려줄 남은 기간
                                    startIndex = nextIdxOfWeek;

                                    currentWeekRow += 1;
                                    break;
                                }
                                //일주일안에 모두 그리기 가능해지면
                                else {
                                    event.setStartIndex(startIndex);
                                    event.setPeriod(tempPeriod);
                                    event.setWeekRow(currentWeekRow);
                                    addEventToFilteredEvents("PERIOD", event, filteredEventList);

                                    idx += (tempPeriod - 1);
                                    tempPeriod = 0;
                                }
                            }
                        }
                    }
                }
            }
            // 반복 일정
            else {
                switch (frequency) {
                    // 매일 반복

                    case "WEEKLY":
                        int startIdx = startIndex;
                        for (Integer day : startDayList) {
                            int diff = day - startDayNum;
                            if (diff < 0) diff += 7; // (ex 수,일 반복인데 수요일부터 시작일 경우)

                            event.setStartIndex(startIndex + diff);//첫 요일 이후 다른 요일들 시작일 계산

                            while (startIdx < end) {
                                if (!(startIdx + diff >= end)) {
                                    addEventToFilteredEvents("WEEKLY", event, filteredEventList);
                                }
                                startIdx += interval * 7;
                                event.setStartIndex(startIdx + diff);
                            }
                        }
                        break;
                    // 월 반복
                    case "MONTHLY":
                        // 몇번째 주 무슨 요일 조건 - startDayNum은 이벤트의 시작 날짜에 따라 결정 ( BYDAY가 아닌)
                        if (setPos != 0) {
                            addDayRecurEventToFilteredEvents(event, filteredEventList, "MONTHLY", setting);
                        }
                        // 마지막 날
                        else if (byMonthDay != 0) {
                            if (startMonth <= currentMonth - 1 || startYear < currentYear) {

                                int firstIndexForPre = DateUtil.getFirstDay(currentYear, currentMonth - 1);
                                int targetIndexForPre = firstIndexForPre + daysOfMonth - 1;
                                int lastIndexForPre = firstIndexForPre + daysOfMonth;

                                if (targetIndexForPre >= lastIndexForPre - DateUtil.getFirstDay(currentYear, currentMonth)
                                        && targetIndexForPre < firstIndexForPre + daysOfMonth) {
                                    event.setStartIndex(DateUtil.getLastDay(currentYear, currentMonth - 1));
                                    addEventToFilteredEvents("MONTHLY", event, filteredEventList);
                                }
                            }

                            int firstDayOfMonth = DateUtil.getFirstDay(currentYear, currentMonth);
                            event.setStartIndex(firstDayOfMonth + DateUtil.daysOfMonth(currentYear, currentMonth) - 1);
                            addEventToFilteredEvents("MONTHLY", event, filteredEventList);
                        }
                        // 마지막 무슨 요일
                        else if (byDayList.size() > 0) {
                            if (startMonth <= currentMonth - 1 || startYear < currentYear) {
                                int firstDayOfMonth = DateUtil.getFirstDay(currentYear, currentMonth - 1);
                                int day = byDayList.get(0).getDay().ordinal();
                                DayOfWeek[] dayOfWeeks = DayOfWeek.values();
                                DayOfWeek dayOfWeek = dayOfWeeks[day - 1];
                                LocalDate date = LocalDate.of(currentYear, currentMonth - 1, 1);
                                int lastDateInMonth = date.with(TemporalAdjusters.lastInMonth(dayOfWeek)).getDayOfMonth();
                                int calculatedIdx = firstDayOfMonth + lastDateInMonth - 1;

                                int lastIndexForPre = firstDayOfMonth + DateUtil.daysOfMonth(currentYear, currentMonth - 1);
                                if (calculatedIdx >= lastIndexForPre - DateUtil.getFirstDay(currentYear, currentMonth) && calculatedIdx < firstDayOfMonth + DateUtil.daysOfMonth(currentYear, currentMonth - 1)) {
                                    event.setStartIndex(startDayNum == 8 ? 0 : startDayNum - 1);
                                    addEventToFilteredEvents("MONTHLY", event, filteredEventList);
                                }
                            }

                            addLastWeekRecurEventToFilteredEvents("MONTHLY", event, filteredEventList, setting);

                        } else {
                            int tempMonth = startMonth;
                            int tempYear = startYear;
                            int tempCount = 0;
                            int j = startIndex;

                            while (j < end) {
                                if (tempCount == interval || tempCount == 0) {
                                    addEventToFilteredEvents("MONTHLY", event, filteredEventList);
                                    tempCount = 0;
                                }

                                int daysForInterval = DateUtil.daysOfMonth(tempYear, tempMonth);
                                j += daysForInterval;
                                event.setStartIndex(j);
                                tempMonth++;
                                tempCount++;

                                if (tempMonth > 12) {
                                    tempMonth = 1;
                                    tempYear++;
                                }
                            }
                        }
                        break;
                    // 연 반복
                    case "YEARLY":
                        //몇번째 주 무슨 요일 조건 - startDayNum은 이벤트의 시작 날짜에 따라 결정 ( BYDAY가 아닌)
                        if (setPos != 0) {
                            addDayRecurEventToFilteredEvents(event, filteredEventList, "YEARLY",setting);
                        }
                        // 마지막 날
                        else if (byMonthDay != 0 && startMonth == currentMonth) {
                            int firstDayOfMonth = DateUtil.getFirstDay(currentYear, currentMonth);
                            event.setStartIndex(firstDayOfMonth + daysOfMonth - 1);
                            addEventToFilteredEvents("YEARLY", event, filteredEventList);
                        }
                        // 마지막 무슨 요일 - setPos가 안들어감
                        else if (byDayList.size() > 0 && startMonth == currentMonth) {
                            addLastWeekRecurEventToFilteredEvents("YEARLY", event, filteredEventList, setting);
                        }
                        // 일반 연 반복
                        else {
                            int tempYear = startYear;
                            int j = startIndex;
                            while (j < end) {
                                int daysForInterval = DateUtil.daysOfYear(startMonth <= 2 ? tempYear : tempYear + 1);

                                addEventToFilteredEvents("YEARLY", event, filteredEventList);

                                j += daysForInterval;
                                event.setStartIndex(j);
                                tempYear++;
                            }
                        }
                        break;
                }
            }
        }

        Collections.sort(filteredEventList,new ICalComparator());

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