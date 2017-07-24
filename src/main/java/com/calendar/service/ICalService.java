package com.calendar.service;

import com.calendar.data.ICalEvent;
import com.calendar.data.ICalFilteredEvent;
import net.fortuna.ical4j.data.CalendarBuilder;
import net.fortuna.ical4j.data.ParserException;
import net.fortuna.ical4j.filter.Filter;
import net.fortuna.ical4j.filter.PeriodRule;
import net.fortuna.ical4j.model.Calendar;
import net.fortuna.ical4j.model.DateTime;
import net.fortuna.ical4j.model.Period;
import net.fortuna.ical4j.model.component.VEvent;
import net.fortuna.ical4j.model.property.RRule;
import org.springframework.stereotype.Service;

import java.io.FileInputStream;
import java.io.IOException;
import java.text.ParseException;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import static java.time.temporal.TemporalAdjusters.firstDayOfMonth;
import static java.time.temporal.TemporalAdjusters.lastDayOfMonth;

/**
 * Created by NAVER on 2017-07-20.
 */
@Service
public class ICalService {

    private static int currentYear;
    private static int currentMonth;

    public static void setCurrenDate(int year, int month) {
        currentYear = year;
        currentMonth = month;
    }

    public static Calendar parseFile(
            String file
    ) throws IOException, ParserException {
        FileInputStream fin = new FileInputStream(file);
        CalendarBuilder builder = new CalendarBuilder();

        return builder.build(fin);
    }

    //일정리스트 만들기
    public List<ICalFilteredEvent> resolveData(Calendar calendar) throws ParseException {

        //전달 23일 부터 다음 달 6일까지
        int preYear = currentMonth == 1 ? currentYear - 1 : currentYear;
        int nextYear = currentMonth == 12 ? currentYear + 1 : currentYear;
        int preMonth = currentMonth == 1 ? 12 : currentMonth - 1;
        int nextMonth = currentMonth == 12 ? 1 : currentMonth + 1;

        YearMonth yearMonth1 = YearMonth.of(preYear, preMonth);//java8
        YearMonth yearMonth2 = YearMonth.of(nextYear, nextMonth);
        LocalDate tempStart = yearMonth1.atDay(23);
        LocalDate tempEnd = yearMonth2.atDay(6);
        DateTime startDate = new DateTime(tempStart.format(DateTimeFormatter.ofPattern("yyyyMMdd")) + "T000000Z");//전달 23일
        DateTime endDate = new DateTime(tempEnd.format(DateTimeFormatter.ofPattern("yyyyMMdd")) + "T000000Z");//다음달 6일

        //기간 만들기 - RRule 밑에 EXPIR있는 경우 인식 못함
        Period period = new Period(startDate, endDate);

        //해당 기간을 일정에 포함하는 이벤트들 리스트에 포함
        List<VEvent> events = calendar.getComponents("VEVENT");

        return filterValidEvents(events, period);
    }

    private List<ICalFilteredEvent> filterValidEvents(List<VEvent> events, Period period) {

        Filter filter = new Filter(new PeriodRule(period));
        events = (List<VEvent>) filter.filter(events);

//        return storeDataToICalEvent(events);
        return filterByIndex(storeDataToICalEvent(events));
    }

    private List<ICalEvent> storeDataToICalEvent(List<VEvent> events) {
        List<ICalEvent> eventList = new ArrayList<>();
        for (VEvent event : events) {

            ICalEvent data = new ICalEvent();
            data.setSummary(event.getSummary().getValue());
            data.setStart(event.getStartDate().getValue());

            data.setStartDate(extractDate(data.getStart()));
            data.setStartMonth(extractMonth(data.getStart()));
            data.setStartYear(extractYear(data.getStart()));
            data.setEnd(event.getEndDate().getValue());

            //반복있는 이벤트의 경우 추가 정보 삽입
            if (event.getProperty("RRULE") != null) {
                RRule rule = (RRule) event.getProperty("RRULE");
                data.setRecur(true);
                data.setInterval(rule.getRecur().getInterval());
                data.setFrequency(rule.getRecur().getFrequency());
                if (rule.getRecur().getUntil() != null) {
                    data.setUntil(rule.getRecur().getUntil().toString());
                    data.setUntilDate(extractDate(data.getUntil()));
                    data.setUntilMonth(extractMonth(data.getUntil()));
                    data.setUntilYear(extractYear(data.getUntil()));
                }
            }
            data.setStartIndex(calculateIndexOfDate(data, "start"));
            data.setEndIndex(calculateIndexOfDate(data, "end"));
            eventList.add(data);
            //System.out.println(event.getSummary().getValue());
        }
        return eventList;
    }

    private List<ICalFilteredEvent> filterByIndex(List<ICalEvent> eventList) {
        List<ICalFilteredEvent> filteredEventList = new ArrayList<>();

        for(ICalEvent event : eventList) {
            ICalFilteredEvent data = new ICalFilteredEvent();
            data.setSummary(event.getSummary());
            data.setStartIndex(event.getStartIndex());
            data.setEndIndex(event.getEndIndex());
            data.setUntilDate(event.getUntil());
            data.setStartYear(event.getStartYear());
            data.setFrequency(event.getFrequency());
            data.setRecur(event.getRecur());
            data.setInterval(event.getInterval());
            data.setStartMonth(event.getStartMonth());

            filteredEventList.add(data);
        }
        return filteredEventList;
    }


    private int calculateIndexOfDate(ICalEvent event, String mode) {
        int index;
        int firstIndex = getFirstDay(event.getStartYear(), event.getStartMonth());

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
            int tempMonth = currentMonth;
            int tempYear = currentYear;

            while (tempYear > eventYear || (tempYear == eventYear && tempMonth > eventMonth)) {

                if (tempMonth == 1) break;
                eventDate -= daysOfMonth(tempYear, tempMonth - 1);
                tempMonth--;

                if (tempMonth == 0) {
                    tempMonth = 12;
                    tempYear--;
                }
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

    private int daysOfMonth(int year, int month) {
        YearMonth ym = YearMonth.of(year, month);
        return  ym.getMonth().length(true);
    }

    private int daysOfYear(int year) {
        return isLeapYear(year) ? 366 : 365;
    }

    private boolean isLeapYear(int year) {
        return year % 400 == 0 || (year % 100 != 0 && year % 4 == 0);
    }

    private int getFirstDay(int year, int month) {
        YearMonth ym = YearMonth.of(year, month);
        return  ym.atDay(1).getDayOfWeek().getValue();
    }

}