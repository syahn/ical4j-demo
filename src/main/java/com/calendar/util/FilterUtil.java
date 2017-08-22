package com.calendar.util;

import com.calendar.data.ICalEvent;
import com.calendar.data.Setting;
import net.fortuna.ical4j.model.Date;
import net.fortuna.ical4j.model.DateTime;
import net.fortuna.ical4j.model.Period;
import net.fortuna.ical4j.model.WeekDayList;

import java.text.ParseException;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjusters;

/**
 * Created by NAVER on 2017-08-22.
 */
public class FilterUtil {
    public static Period makeValidPeriod(int year, int month) throws ParseException {
        int preYear = DateUtil.getYearOfPreMonth(year, month);
        int nextYear = DateUtil.getYearOfNextMonth(year, month);
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

    // 마지막째 주 요일 반복
    public static int calIndexOfLastWeekRecurEvent(ICalEvent event, Setting setting) {
        int currentYear = setting.getCurrentYear();
        int currentMonth = setting.getCurrentMonth();
        WeekDayList byDayList = event.getByDayList();
        int firstDayOfMonth = DateUtil.getFirstDay(currentYear, currentMonth);
        int day = byDayList.get(0).getDay().ordinal();
        DayOfWeek[] dayOfWeeks = DayOfWeek.values();
        DayOfWeek dayOfWeek = dayOfWeeks[day - 1];
        LocalDate date = LocalDate.of(currentYear, currentMonth, 1);
        int lastDateInMonth = date.with(TemporalAdjusters.lastInMonth(dayOfWeek)).getDayOfMonth();

        return firstDayOfMonth + lastDateInMonth - 1;
    }

    // 몇째 주 요일 반복
    public static int calIndexOfDayRecurEvent(ICalEvent event, int year, int month) {
        int startDayNum = event.getStartDayNum();
        int setPos = event.getBySetPos();
        int firstIndex = DateUtil.getFirstDay(year, month);

        if (startDayNum > firstIndex) { // 테이블의 0번째 row에 해당 요일이 포함되는 경우
            return startDayNum + 7 * (setPos - 1) - 1;
        } else {
            return startDayNum + 7 * setPos - 1;
        }
    }

    public static int calIndexOfDate(ICalEvent event, String mode, Setting setting) {
        int index;
        int currentYear = setting.getCurrentYear();
        int currentMonth = setting.getCurrentMonth();
        int firstIndex = DateUtil.getFirstDay(currentYear, currentMonth);

        int eventYear = mode.equals("start") ? event.getStartYear() : event.getUntilYear();
        int eventMonth = mode.equals("start") ? event.getStartMonth() : event.getUntilMonth();
        int eventDate = mode.equals("start") ? event.getStartDate() : event.getUntilDate();

        //기간 일정의 종료날자 (DTEND)
        if (mode.equals("end")) {
            eventYear = event.getEndYear();
            eventMonth = event.getEndMonth();
            eventDate = event.getEndDate();
        }

        //현재 달에서 이벤트 시작시
        if (eventMonth == currentMonth && eventYear == currentYear) {
            index = eventDate + firstIndex - 1;
        }

        //현재 달 이후에 이벤트 시작시(최대 6일까지만 존재)
        else if ((eventMonth > currentMonth && eventYear == currentYear) || eventYear > currentYear) {
            index = eventDate + firstIndex - 1 + DateUtil.daysOfMonth(currentYear, currentMonth);
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

                eventDate -= DateUtil.daysOfMonth(tempYear, tempMonth);
                tempMonth--;
            }
            index = eventDate + firstIndex - 1;
        }
        return index;
    }


}
