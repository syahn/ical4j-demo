package com.calendar.service;

import com.calendar.data.ICalEvent;
import com.calendar.data.ICalFilteredEvent;
import net.fortuna.ical4j.data.CalendarBuilder;
import net.fortuna.ical4j.data.ParserException;
import net.fortuna.ical4j.filter.Filter;
import net.fortuna.ical4j.filter.PeriodRule;
import net.fortuna.ical4j.model.*;
import net.fortuna.ical4j.model.component.VEvent;
import net.fortuna.ical4j.model.property.RRule;
import org.springframework.stereotype.Service;

import java.io.FileInputStream;
import java.io.IOException;
import java.text.ParseException;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjuster;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.List;

import static java.time.temporal.TemporalAdjusters.lastDayOfMonth;

/**
 * Created by NAVER on 2017-07-20.
 */
@Service
public class ICalService {
    private static int currentYear;
    private static int currentMonth;

    public Calendar parseFile(String file) throws IOException, ParserException {
        FileInputStream fin = new FileInputStream(file);
        CalendarBuilder builder = new CalendarBuilder();

        return builder.build(fin);
    }

    //일정리스트 만들기
    public List<ICalFilteredEvent> filterData(Calendar calendar, int month) throws ParseException {
        setCurrentDate(currentYear, month);//만약 기간 옵션이 연범위로 늘어나면 current year에 대한 인자도 받아야함

        Period validPeriod = makeValidPeriod(currentYear, month); //전달 23일 부터 다음 달 6일까지의 기간 설정
        List<VEvent> events = calendar.getComponents("VEVENT"); //해당 기간을 일정에 포함하는 이벤트들 리스트에 포함

        return filterValidEvents(events, validPeriod);
    }

    public void setCurrentDate(int year, int month) {
        currentYear = year;
        currentMonth = month;
    }

    private Period makeValidPeriod(int year, int month) throws ParseException {
        int preYear = getYearOfPreMonth(year, month);
        int nextYear = getYearOfNextMonth(year, month);
        int preMonth = getPreMonth(month);
        int nextMonth = getNextMonth(month);

        LocalDate tempStart = YearMonth.of(preYear, preMonth).atDay(23);
        LocalDate tempEnd = YearMonth.of(nextYear, nextMonth).atDay(6);
        DateTime startDate = new DateTime(tempStart.format(DateTimeFormatter.ofPattern("yyyyMMdd")) + "T000000Z");//전달 23일
        DateTime endDate = new DateTime(tempEnd.format(DateTimeFormatter.ofPattern("yyyyMMdd")) + "T000000Z");//다음달 6일

        //기간 만들기 - RRule 밑에 EXPIR있는 경우 인식 못함
        return new Period(startDate, endDate);
    }

    private List<ICalFilteredEvent> filterValidEvents(List<VEvent> events, Period period) {
        Filter filter = new Filter(new PeriodRule(period));
        events = (List<VEvent>) filter.filter(events);

        return filterByIndex(storeDataToICalEvent(events));
    }

    private List<ICalEvent> storeDataToICalEvent(List<VEvent> events) {
        List<ICalEvent> eventList = new ArrayList<>();

        for (VEvent event : events) {
            ICalEvent data = new ICalEvent();
            data.setUid(event.getUid().getValue());
            data.setSummary(event.getSummary().getValue());
            data.setStart(event.getStartDate().getValue());
            data.setStartDate(extractDate(data.getStart()));
            data.setStartMonth(extractMonth(data.getStart()));
            data.setStartYear(extractYear(data.getStart()));
            data.setEnd(event.getEndDate().getValue());
            data.setStartIndex(calculateIndexOfDate(data, "start"));//모든 이벤트 필수

            //반복있는 이벤트의 경우 추가 정보 삽입
            if (event.getProperty("RRULE") != null) {
                RRule rule = (RRule) event.getProperty("RRULE");
                data.setRecur(true);
                data.setInterval(rule.getRecur().getInterval());
                data.setFrequency(rule.getRecur().getFrequency());
                data.setByDayList(rule.getRecur().getDayList());
                if (rule.getRecur().getUntil() != null) {
                    data.setUntil(rule.getRecur().getUntil().toString());
                    data.setUntilDate(extractDate(data.getUntil()));
                    data.setUntilMonth(extractMonth(data.getUntil()));
                    data.setUntilYear(extractYear(data.getUntil()));
                    data.setEndIndex(calculateIndexOfDate(data, "end"));//end index는 until있을 때만 필요
                }
                /* 요일 반복 위한 이벤트 시작 날짜들 리스트(일,금 이면 1,6) */
                if (rule.getRecur().getDayList() != null) {//요일 반복일때만 daylist 있음

                    ArrayList<Integer> tempDayList = new ArrayList<>();
                    for (WeekDay day : rule.getRecur().getDayList()) {
                        tempDayList.add(WeekDay.getCalendarDay(day));
//                        System.out.println(event.getSummary()+day.toString());
                    }
                    data.setStartDayList(tempDayList);


                    //시작 날짜의 요일 dayofWeek 포함( 나중에 시작일 구분 시 필요) - 시작일이 요일이면
                    LocalDate date = LocalDate.of(data.getStartYear(), data.getStartMonth(), data.getStartDate());
                    DayOfWeek dayOfWeek = date.getDayOfWeek();
                    int startDayNum = dayOfWeek.getValue();
                    System.out.println(event.getSummary()+Integer.toString(startDayNum));
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

    private List<ICalFilteredEvent> filterByIndex(List<ICalEvent> eventList) {
        List<ICalFilteredEvent> filteredEventList = new ArrayList<>();

        for (ICalEvent event : eventList) {
            String frequency = event.getFrequency();
            int startIndex = event.getStartIndex();
            int endIndex = event.getEndIndex();
            List<Integer> startDayList = event.getStartDayList();
            int startDayNum = event.getStartDayNum();
            int interval = event.getInterval();
            int startYear = event.getStartYear();
            int startMonth = event.getStartMonth();
            int untilDate = event.getUntilDate();
            int setPos = event.getBySetPos();
            boolean recur = event.getRecur();
            int byMonthDay = event.getByMonthDay();
            WeekDayList byDayList = event.getByDayList();

            int firstDayOfMonth = getFirstDay(currentYear, currentMonth);
            int end = untilDate == 0 ? 42 : endIndex + 1;

            if (recur == false) {
                addEventToFilteredEvents("DAY", event, filteredEventList);
            } else {
                if (frequency.equals("DAILY")) {
                    int j = startIndex;
                    while (j < end) {
                        addEventToFilteredEvents("DAILY", event, filteredEventList);
                        j += interval;
                        event.setStartIndex(j);
                    }
                } else if (frequency.equals("WEEKLY")) {
                    for (int d = 0; d < startDayList.size(); d++) {
                        int diff = startDayList.get(d) - startDayNum;
                        if (diff < 0) {
                            diff += 7; // (ex 수,일 반복인데 수요일부터 시작일 경우)
                        }
                        event.setStartIndex(startIndex + diff);//첫 요일 이후 다른 요일들 시작일 계산

                        for (int j = startIndex; j < end; ) {
                            if (!(j + diff >= end)) {
                                addEventToFilteredEvents("WEEKLY", event, filteredEventList);
                            }
                            j += interval * 7;
                            event.setStartIndex(j + diff);
                        }
                    }
                } else if (frequency.equals("MONTHLY")) {
                    if (setPos != 0) {//몇번째 주 무슨 요일 조건 - startDayNum은 이벤트의 시작 날짜에 따라 결정 ( BYDAY가 아닌)
                        addDayRecurEventToFilteredEvents(event, filteredEventList, "MONTHLY");
                    }
                    // 몇 번째 요일
                    else if (byMonthDay != 0){
                        event.setStartIndex(firstDayOfMonth + daysOfMonth(currentYear, currentMonth));
                        addEventToFilteredEvents("MONTHLY", event, filteredEventList);
                    }
                    // 마지막 무슨 요일
                    else if (byDayList.size() > 0) {
                        int day = byDayList.get(0).getDay().ordinal();
                        DayOfWeek[] dayOfWeeks = DayOfWeek.values();
                        DayOfWeek dayOfWeek = dayOfWeeks[day-1];
                        LocalDate date = LocalDate.of(currentYear, currentMonth, 1);
                        int lastDateInMonth = date.with(TemporalAdjusters.lastInMonth(dayOfWeek)).getDayOfMonth();
                        int calculatedIdx = firstDayOfMonth +  lastDateInMonth - 1;
                        event.setStartIndex(calculatedIdx);
                        addEventToFilteredEvents("MONTHLY", event, filteredEventList);
                    }
                    else {
                        int tempMonth = startMonth;
                        int tempYear = startYear;
                        int tempCount = 0;

                        int j = startIndex;
                        while (j < end) {
                            if (tempCount == interval || tempCount == 0) {
                                addEventToFilteredEvents("MONTHLY", event, filteredEventList);
                                tempCount = 0;
                            }

                            int daysForInterval = daysOfMonth(tempYear, tempMonth);
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
                } else if (frequency.equals("YEARLY")) {
                    if (setPos != 0) {//몇번째 주 무슨 요일 조건 - startDayNum은 이벤트의 시작 날짜에 따라 결정 ( BYDAY가 아닌)
                        addDayRecurEventToFilteredEvents(event, filteredEventList, "YEARLY");
                    } else { // 일반 연 반복
                        int tempYear = startYear;
                        int j = startIndex;
                        while (j < end) {
                            int daysForInterval = daysOfYear(startMonth <= 2 ? tempYear : tempYear + 1);

                            addEventToFilteredEvents("YEARLY", event, filteredEventList);

                            j += daysForInterval;
                            event.setStartIndex(j);
                            tempYear++;
                        }
                    }
                }
            }
        }
        return filteredEventList;
    }

    private void addDayRecurEventToFilteredEvents(
            ICalEvent event,
            List<ICalFilteredEvent> filteredEventList,
            String type
    ) {
        int startDayNum = event.getStartDayNum();
        int startMonth = event.getStartMonth();
        int setPos = event.getBySetPos();
        int preYear = getYearOfPreMonth(currentYear, currentMonth);
        int nextYear = getYearOfNextMonth(currentYear, currentMonth);
        int preMonth = getPreMonth(currentMonth);
        int nextMonth = getNextMonth(currentMonth);

        if (startMonth == currentMonth) {
            int firstIndex = getFirstDay(currentYear, currentMonth);
            int targetIndex;
            if (startDayNum > firstIndex) { // 테이블의 0번째 row에 해당 요일이 포함되는 경우
                targetIndex = startDayNum + 7 * (setPos - 1) - 1;
            } else {
                targetIndex = startDayNum + 7 * setPos - 1;
            }
            event.setStartIndex(targetIndex);
            addEventToFilteredEvents(type, event, filteredEventList);
        } else if (startMonth == preMonth) {
            //4,5째주면 표시가능
            if (setPos == 4 || setPos == 5) {
                int firstIndex = getFirstDay(preYear, startMonth);
                int targetIndex;

                if (startDayNum > firstIndex) { // 테이블의 0번째 row에 해당 요일이 포함되는 경우
                    targetIndex = startDayNum + 7 * (setPos - 1) - 1;
                } else {
                    targetIndex = startDayNum + 7 * setPos - 1;
                }

                if (targetIndex >= 28 && targetIndex < 34) {
                    event.setStartIndex(startDayNum - 1);
                    addEventToFilteredEvents(type, event, filteredEventList);
                }
            }
        } else if (startMonth == nextMonth) {

            if (setPos == 1) {
                int firstIndex = getFirstDay(nextYear, startMonth);
                int currentFirstIndex = getFirstDay(currentYear, currentMonth);
                int currentLastDay = getLastDay(currentYear, currentMonth);
                int daysToAdd = daysOfMonth(currentYear, currentMonth) - 1;
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

    private void addEventToFilteredEvents(
            String type,
            ICalEvent event,
            List<ICalFilteredEvent> filteredEventList
    ) {
        int startIndex = event.getStartIndex();
        int endIndex = event.getEndIndex();

        if (startIndex >= 0 && startIndex < 42 || endIndex >= 0 && endIndex < 42) {
            ICalFilteredEvent data = new ICalFilteredEvent();
            data.setSummary(event.getSummary());
            data.setIndex(startIndex);
            data.setUid(event.getUid());
            data.setType(type);
            filteredEventList.add(data);
        }
    }

    private int calculateIndexOfDate(ICalEvent event, String mode) {
        int index;
        int firstIndex = getFirstDay(currentYear, currentMonth);

        int eventYear = mode.equals("start") ? event.getStartYear() : event.getUntilYear();
        int eventMonth = mode.equals("start") ? event.getStartMonth() : event.getUntilMonth();
        int eventDate = mode.equals("start") ? event.getStartDate() : event.getUntilDate();

        //현재 달에서 이벤트 시작시
        if (eventMonth == currentMonth && eventYear == currentYear) {
            index = eventDate + firstIndex - 1;
        }

        //현재 달 이후에 이벤트 시작시(최대 6일까지만 존재)
        else if ((eventMonth > currentMonth && eventYear == currentYear) || eventYear > currentYear) {
            index = eventDate + firstIndex - 1 + daysOfMonth(currentYear, currentMonth);
        }

        //현재 달 이전에 이벤트가 시작시(얼마나 이전인지 한계 없음)
        else {
            int tempMonth = currentMonth - 1;
            int tempYear = currentYear;

            while (tempYear > eventYear || (tempYear == eventYear && tempMonth >= eventMonth)) {

                if (tempMonth == 0) {
                    tempMonth = 12;
                    tempYear--;
                }

                eventDate -= daysOfMonth(tempYear, tempMonth);
                tempMonth--;
            }
            index = eventDate + firstIndex - 1;
        }
        return index;
    }


    private int extractYear(String time) {
        return Integer.parseInt(time.substring(0, 4));
    }

    private int extractMonth(String time) {
        return Integer.parseInt(time.substring(4, 6));
    }

    private int extractDate(String time) {
        return Integer.parseInt(time.substring(6, 8));
    }

    private boolean isLeapYear(int year) {
        return year % 400 == 0 || (year % 100 != 0 && year % 4 == 0);
    }

    private int daysOfMonth(int year, int month) {
        YearMonth ym = YearMonth.of(year, month);
        return ym.getMonth().length(isLeapYear(year));
    }

    private int daysOfYear(int year) {
        return isLeapYear(year) ? 366 : 365;
    }

    private int getFirstDay(int year, int month) {
        YearMonth ym = YearMonth.of(year, month);
        return ym.atDay(1).getDayOfWeek().getValue();
    }

    private int getLastDay(int year, int month) {
        YearMonth ym = YearMonth.of(year, month);
        return ym.atDay(daysOfMonth(year, month)).getDayOfWeek().getValue();
    }

    private int getYearOfPreMonth(int year, int month) {
        return month == 1 ? year - 1 : year;
    }

    private int getYearOfNextMonth(int year, int month) {
        return month == 12 ? year + 1 : year;
    }

    private int getPreMonth(int month) {
        return month == 1 ? 12 : month - 1;
    }

    private int getNextMonth(int month) {
        return month == 12 ? 1 : month + 1;
    }
}